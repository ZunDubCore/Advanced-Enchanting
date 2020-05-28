package zundubcore.advancedenchanting.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import zundubcore.advancedenchanting.block.AdvancedEnchantingTableBlock;
import zundubcore.advancedenchanting.container.AdvancedEnchantingTableContainer;
import zundubcore.advancedenchanting.init.AdvancedEnchantingBlocks;
import zundubcore.advancedenchanting.init.AdvancedEnchantingTileEntityTypes;

public class AdvancedEnchantingTableTileEntity extends FloatingBookTileEntity implements
        INamedContainerProvider {

    public AdvancedEnchantingTableTileEntity() {
        super(AdvancedEnchantingTileEntityTypes.ADVANCED_ENCHANTING_TABLE.get());
    }

    /**
     * Called from {@link NetworkHooks#openGui} (which is called from {@link
     * AdvancedEnchantingTableBlock#onBlockActivated} on the logical server)
     *
     * @return The logical-server-side Container for this TileEntity
     */
    @Override
    public Container createMenu(final int windowId, final PlayerInventory inventory,
            final PlayerEntity player) {

        return new AdvancedEnchantingTableContainer(windowId, inventory, this);
    }

    @Override
    public ITextComponent getDisplayName() {

        return new TranslationTextComponent(
                AdvancedEnchantingBlocks.ADVANCED_ENCHANTING_TABLE.get().getTranslationKey());
    }
}
