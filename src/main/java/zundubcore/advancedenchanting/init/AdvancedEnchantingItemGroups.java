package zundubcore.advancedenchanting.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import zundubcore.advancedenchanting.AdvancedEnchanting;

/**
 * This class holds all our ItemGroups (Formerly called CreativeTabs). Static initialisers are fine
 * here.
 */
public final class AdvancedEnchantingItemGroups {

    public static final ItemGroup ADVANCED_ENCHANTING_ITEM_GROUP = new AdvancedEnchantingItemGroup(
            AdvancedEnchanting.MODID,
            () -> new ItemStack(AdvancedEnchantingBlocks.ADVANCED_ENCHANTING_TABLE.get()));

    // Main tab
    public static final class AdvancedEnchantingItemGroup extends ItemGroup {

        @Nonnull
        private final Supplier<ItemStack> iconSupplier;

        public AdvancedEnchantingItemGroup(@Nonnull final String name,
                @Nonnull final Supplier<ItemStack> iconSupplier) {

            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Override
        @Nonnull
        public ItemStack createIcon() {
            return iconSupplier.get();
        }

        @Override
        public String getTranslationKey() {
            return "tab." + AdvancedEnchanting.MODID + ".main";
        }
    }
}
