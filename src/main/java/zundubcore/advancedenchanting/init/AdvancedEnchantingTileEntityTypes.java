package zundubcore.advancedenchanting.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.tileentity.AdvancedEnchantingTableTileEntity;
import zundubcore.advancedenchanting.tileentity.DecorativeBookTileEntity;

/**
 * Holds a list of all our {@link TileEntityType}s. Suppliers that create TileEntityTypes are added
 * to the DeferredRegister. The DeferredRegister is then added to our mod event bus in our
 * constructor. When the TileEntityType Registry Event is fired by Forge and it is time for the mod
 * to register its TileEntityTypes, our TileEntityTypes are created and registered by the
 * DeferredRegister. The TileEntityType Registry Event will always be called after the Block and
 * Item registries are filled. Note: This supports registry overrides.
 */
public final class AdvancedEnchantingTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =
            new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, AdvancedEnchanting.MODID);

    public static final RegistryObject<TileEntityType<AdvancedEnchantingTableTileEntity>>
            ADVANCED_ENCHANTING_TABLE = TILE_ENTITY_TYPES.register("advanced_enchanting_table",
            () -> TileEntityType.Builder.create(AdvancedEnchantingTableTileEntity::new,
                    AdvancedEnchantingBlocks.ADVANCED_ENCHANTING_TABLE.get()).build(null));

    public static final RegistryObject<TileEntityType<DecorativeBookTileEntity>>
            DECORATIVE_BOOK = TILE_ENTITY_TYPES.register("decorative_book",
            () -> TileEntityType.Builder.create(DecorativeBookTileEntity::new,
                    AdvancedEnchantingBlocks.DECORATIVE_BOOK.get()).build(null));
}
