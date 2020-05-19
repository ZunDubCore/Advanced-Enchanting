package zundubcore.advancedenchanting.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import zundubcore.advancedenchanting.AdvancedEnchanting;

/**
 * Subscribe to events from the FORGE EventBus that should be handled on the PHYSICAL CLIENT side in
 * this class
 */
@EventBusSubscriber(modid = AdvancedEnchanting.MODID, bus = EventBusSubscriber.Bus.FORGE, value =
        Dist.CLIENT)

public final class ClientForgeEventSubscriber {

}
