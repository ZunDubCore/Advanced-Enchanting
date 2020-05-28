package zundubcore.advancedenchanting.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import zundubcore.advancedenchanting.config.AdvancedEnchantingConfig;
import zundubcore.advancedenchanting.init.AdvancedEnchantingTileEntityTypes;
import zundubcore.advancedenchanting.tileentity.DecorativeBookTileEntity;

public class DecorativeBookBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Block.makeCuboidShape(4.8D, 9.6D, 4.8D, 9.6D, 14.4D,
            9.6D);

    public DecorativeBookBlock(Properties builder) {
        super(builder);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return AdvancedEnchantingTileEntityTypes.DECORATIVE_BOOK.get().create();
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {

        return (float) AdvancedEnchantingConfig.floatingBookBonus;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
            ISelectionContext context) {
        if (worldIn.getTileEntity(pos) instanceof DecorativeBookTileEntity) {
            return SHAPE.withOffset(0,
                    ((DecorativeBookTileEntity) worldIn.getTileEntity(pos)).height, 0);
        }

        return SHAPE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos,
            PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {

        if (!worldIn.isRemote && !playerIn.getHeldItemMainhand().isEmpty()
                && worldIn.getTileEntity(pos) instanceof DecorativeBookTileEntity) {

            final DecorativeBookTileEntity deco = (DecorativeBookTileEntity) worldIn.getTileEntity(
                    pos);

            if (playerIn.getHeldItem(hand).getItem() == Items.FEATHER) {
                deco.increaseHeight();
            }
            else if (playerIn.getHeldItem(hand).getItem() == Items.IRON_INGOT) {
                deco.decreaseHeight();
            }

            worldIn.notifyBlockUpdate(pos, state, state, 8);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer,
            ItemStack stack) {

        final TileEntity tile = worldIn.getTileEntity(pos);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
            TileEntity tileEntity,
            ItemStack stack) {

        if (tileEntity instanceof DecorativeBookTileEntity) {
            final DecorativeBookTileEntity deco = (DecorativeBookTileEntity) tileEntity;
            spawnAsEntity(worldIn, pos, this.getData(deco));
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {

        final ItemStack itemstack = this.getData(
                (DecorativeBookTileEntity) worldIn.getTileEntity(pos));
        return itemstack != null ? itemstack : new ItemStack(this);
    }

    public ItemStack getData(DecorativeBookTileEntity tile) {

        final ItemStack stack = new ItemStack(this, 1);
        final CompoundNBT tag = new CompoundNBT();
        tag.putFloat("Height", tile.height);
        tag.putInt("Color", tile.color);
        stack.setTag(tag);
        return stack;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {

        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {

        final TileEntity tile = worldIn.getTileEntity(pos);

        return tile instanceof DecorativeBookTileEntity &&
                ((DecorativeBookTileEntity) tile).isOpen() ? 15 : 0;
    }
}
