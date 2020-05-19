package zundubcore.advancedenchanting.init;

import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import zundubcore.advancedenchanting.AdvancedEnchanting;

/**
 * Holds a list of all our {@link EntityType}s. Suppliers that create EntityTypes are added to the
 * DeferredRegister. The DeferredRegister is then added to our mod event bus in our constructor.
 * When the EntityType Registry Event is fired by Forge and it is time for the mod to register its
 * EntityTypes, our EntityTypes are created and registered by the DeferredRegister. The EntityType
 * Registry Event will always be called after the Block and Item registries are filled. Note: This
 * supports registry overrides.
 */
public final class AdvancedEnchantingEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(
            ForgeRegistries.ENTITIES, AdvancedEnchanting.MODID);

    // Currently no Entities
}
