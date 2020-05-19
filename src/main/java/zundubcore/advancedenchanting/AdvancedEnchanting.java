package zundubcore.advancedenchanting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

import zundubcore.advancedenchanting.api.Blacklist;
import zundubcore.advancedenchanting.config.ConfigHolder;
import zundubcore.advancedenchanting.init.AdvancedEnchantingBlocks;
import zundubcore.advancedenchanting.init.AdvancedEnchantingContainerTypes;
import zundubcore.advancedenchanting.init.AdvancedEnchantingEntityTypes;
import zundubcore.advancedenchanting.init.AdvancedEnchantingItems;
import zundubcore.advancedenchanting.init.AdvancedEnchantingTileEntityTypes;
import zundubcore.advancedenchanting.network.PacketHandler;

@Mod(AdvancedEnchanting.MODID)
public final class AdvancedEnchanting {

    public static final String MODID = "advancedenchanting";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final Predicate<ItemStack> TEST_ENCHANTABILITY =
            (stack) -> !Blacklist.isItemBlacklisted(stack) && (stack.isEnchantable()
                    || stack.isEnchanted() || stack.getItem() == net.minecraft.item.Items.BOOK
                    || stack.getItem() == net.minecraft.item.Items.ENCHANTED_BOOK);

    public static AdvancedEnchanting instance;

    public static PacketHandler packetHandler = new PacketHandler();

    public final String version;

    public AdvancedEnchanting() {
        instance = this;

        LOGGER.debug("Hello from" + MODID);

        version = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();

        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        // Register Deferred Registers (Does not need to be before Configs)
        AdvancedEnchantingItems.ITEMS.register(modEventBus);
        AdvancedEnchantingBlocks.BLOCKS.register(modEventBus);
        AdvancedEnchantingContainerTypes.CONTAINER_TYPES.register(modEventBus);
        AdvancedEnchantingEntityTypes.ENTITY_TYPES.register(modEventBus);
        AdvancedEnchantingTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        // Register Configs (Does not need to be after Deferred Registers)
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // I have no idea when network code should be initialized, so I'll just do it here
        packetHandler.initialize();
    }
}
