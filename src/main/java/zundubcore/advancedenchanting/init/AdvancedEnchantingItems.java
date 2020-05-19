package zundubcore.advancedenchanting.init;

import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import zundubcore.advancedenchanting.AdvancedEnchanting;

/**
 * Holds a list of all our {@link Item}s. Suppliers that create Items are added to the
 * DeferredRegister. The DeferredRegister is then added to our mod event bus in our constructor.
 * When the Item Registry Event is fired by Forge and it is time for the mod to register its Items,
 * our Items are created and registered by the DeferredRegister. The Item Registry Event will always
 * be called after the Block registry is filled. Note: This supports registry overrides.
 */
public final class AdvancedEnchantingItems {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
            AdvancedEnchanting.MODID);

    // Currently no items
    // TODO: Add book items and table upgrade
}
