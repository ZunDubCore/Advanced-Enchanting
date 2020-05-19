package zundubcore.advancedenchanting.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

import javax.annotation.Nullable;

import zundubcore.advancedenchanting.init.AdvancedEnchantingTileEntityTypes;
import zundubcore.advancedenchanting.tileentity.AdvancedEnchantingTableTileEntity;

public class AdvancedEnchantingTableBlock extends ContainerBlock {

    public static final VoxelShape SHAPE = Block
            .makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public AdvancedEnchantingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity(final BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
            ISelectionContext context) {

        return SHAPE;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return AdvancedEnchantingTileEntityTypes.ADVANCED_ENCHANTING_TABLE.get().create();
    }

    /**
     * Called on the logical server when a BlockState with a TileEntity is replaced by another
     * BlockState. We use this method to drop all the items from our tile entity's inventory and
     * update comparators near our block.
     * <p>
     * TODO: make table inventory work like a chest, hold item
     *
     * @deprecated Call via {@link BlockState#onReplaced(World, BlockPos, BlockState, boolean)}
     * Implementing/overriding is fine.
     */
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState,
            boolean isMoving) {

        //		if (state.getBlock() != newState.getBlock()) {
        //			TileEntity tileEntity = worldIn.getTileEntity(pos);
        //			if (tileEntity instanceof AdvancedEnchantingTableTileEntity) {
        //				final ItemStackHandler inventory = ((AdvancedEnchantingTableTileEntity)
        //				tileEntity).inventory;
        //				for (int slot = 0; slot < inventory.getSlots(); ++slot)
        //					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos
        //					.getZ(),
        //							inventory.getStackInSlot(slot));
        //			}

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire
     * particles). Note that this method is unrelated to {@link randomTick} and {@link
     * #needsRandomTick}, and will always be called regardless of whether the block can receive
     * random update ticks
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        super.animateTick(stateIn, worldIn, pos, rand);

        for (int i = -2; i <= 2; ++i) {

            for (int j = -2; j <= 2; ++j) {

                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }

                if (rand.nextInt(16) == 0) {

                    for (int k = 0; k <= 1; ++k) {

                        BlockPos blockpos = pos.add(i, k, j);

                        if (worldIn.getBlockState(blockpos).getEnchantPowerBonus(worldIn, pos)
                                > 0) {

                            if (!worldIn.isAirBlock(pos.add(i / 2, 0, j / 2))) {
                                break;
                            }

                            worldIn.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5D,
                                    (double) pos.getY() + 2.0D, (double) pos.getZ() + 0.5D,
                                    (double) ((float) i + rand.nextFloat()) - 0.5D,
                                    (float) k - rand.nextFloat() - 1.0F,
                                    (double) ((float) j + rand.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model,
     * MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all
     * rendering
     *
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible.
     * Implementing/overriding is fine.
     */
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * Called when a player right clicks our block. We use this method to open our gui.
     *
     * @deprecated Call via {@link BlockState#onBlockActivated(World, PlayerEntity, Hand,
     * BlockRayTraceResult)} whenever possible. Implementing/overriding is fine.
     */
    @Override
    public ActionResultType onBlockActivated(final BlockState state, final World worldIn,
            final BlockPos pos, final PlayerEntity player, final Hand handIn,
            final BlockRayTraceResult hit) {

        if (!worldIn.isRemote) {

            final TileEntity tileEntity = worldIn.getTileEntity(pos);

            if (tileEntity instanceof AdvancedEnchantingTableTileEntity) {

                NetworkHooks.openGui((ServerPlayerEntity) player,
                        (AdvancedEnchantingTableTileEntity) tileEntity, pos);
            }
        }

        return ActionResultType.SUCCESS;
    }
}
