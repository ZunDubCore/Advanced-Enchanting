package zundubcore.advancedenchanting.client;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.client.gui.AdvancedEnchantingTableScreen;
import zundubcore.advancedenchanting.client.render.tileentity.AdvancedEnchantingTableTileEntityRenderer;
import zundubcore.advancedenchanting.init.AdvancedEnchantingContainerTypes;
import zundubcore.advancedenchanting.init.AdvancedEnchantingTileEntityTypes;

/**
 * Subscribe to events from the MOD EventBus that should be handled on the PHYSICAL CLIENT side in
 * this class
 */
@EventBusSubscriber(modid = AdvancedEnchanting.MODID, bus = EventBusSubscriber.Bus.MOD, value =
        Dist.CLIENT)

public final class ClientModEventSubscriber {

    public static final ResourceLocation DECORATIVE_BOOK = new ResourceLocation(
            AdvancedEnchanting.MODID, "entity/decorative_book");

    private static final Logger LOGGER = LogManager.getLogger(
            AdvancedEnchanting.MODID + " Client Mod Event Subscriber");

    /**
     * We need to register our renderers on the client because rendering code does not exist on the
     * server and trying to use it on a dedicated server will crash the game.
     * <p>
     * This method will be called by Forge when it is time for the mod to do its client-side setup
     * This method will always be called after the Registry events. This means that all Blocks,
     * Items, TileEntityTypes, etc. will all have been registered already
     */
    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {

        // Register TileEntity Renderers
        ClientRegistry.bindTileEntityRenderer(
                AdvancedEnchantingTileEntityTypes.ADVANCED_ENCHANTING_TABLE.get(),
                AdvancedEnchantingTableTileEntityRenderer::new);

        LOGGER.debug("Registered TileEntity Renderers");

        // Register Entity Renderers
        // Currently no Entities

        // Register ContainerType Screens
        // ScreenManager.registerFactory is not safe to call during parallel mod loading
        // so we queue it to run later

        // TODO: Overcome deprecation
        DeferredWorkQueue.runLater(() -> {
            ScreenManager.registerFactory(
                    AdvancedEnchantingContainerTypes.ADVANCED_ENCHANTING_TABLE.get(),
                    AdvancedEnchantingTableScreen::new);
            LOGGER.debug("Registered ContainerType Screens");
        });
    }

    /**
     * Register texture atlas for our book moodle
     */
    @SubscribeEvent
    public static void onStitchEvent(TextureStitchEvent.Pre event) {

        ResourceLocation stitching = event.getMap().getTextureLocation();

        if (!stitching.equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }

        event.addSprite(DECORATIVE_BOOK);
    }
}
