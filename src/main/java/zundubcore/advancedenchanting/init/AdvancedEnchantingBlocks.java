package zundubcore.advancedenchanting.init;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.block.AdvancedEnchantingTableBlock;

/**
 * Holds a list of all our {@link Block}s. Suppliers that create Blocks are added to the
 * DeferredRegister. The DeferredRegister is then added to our mod event bus in our constructor.
 * When the Block Registry Event is fired by Forge and it is time for the mod to register its
 * Blocks, our Blocks are created and registered by the DeferredRegister. The Block Registry Event
 * will always be called before the Item registry is filled. Note: This supports registry
 * overrides.
 */
public final class AdvancedEnchantingBlocks {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(
            ForgeRegistries.BLOCKS, AdvancedEnchanting.MODID);

    public static final RegistryObject<Block> ADVANCED_ENCHANTING_TABLE = BLOCKS.register(
            "advanced_enchanting_table", () -> new AdvancedEnchantingTableBlock(
                    Block.Properties.create(Material.ROCK).hardnessAndResistance(5.0F, 2000.0F)));
}
