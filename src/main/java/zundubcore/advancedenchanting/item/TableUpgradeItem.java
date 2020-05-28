package zundubcore.advancedenchanting.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import zundubcore.advancedenchanting.init.AdvancedEnchantingBlocks;
import zundubcore.advancedenchanting.tileentity.AdvancedEnchantingTableTileEntity;

public class TableUpgradeItem extends Item {

    public TableUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        final World world = context.getWorld();

        final BlockPos pos = context.getPos();
        final Block block = world.getBlockState(pos).getBlock();

        final PlayerEntity player = context.getPlayer();
        final Hand hand = context.getHand();

        if (block == Blocks.ENCHANTING_TABLE) {

            world.setBlockState(pos,
                    AdvancedEnchantingBlocks.ADVANCED_ENCHANTING_TABLE.get().getDefaultState());
            world.setTileEntity(pos, new AdvancedEnchantingTableTileEntity());

            if (!player.isCreative()) {
                player.getHeldItem(hand).shrink(1);
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
