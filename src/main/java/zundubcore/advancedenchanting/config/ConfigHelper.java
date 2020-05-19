package zundubcore.advancedenchanting.config;

import net.minecraftforge.fml.config.ModConfig;

/**
 * This bakes the config values to normal fields
 * <p>
 * It can be merged into the main ExampleModConfig class, but is separate because of personal
 * preference and to keep the code organised
 */
public final class ConfigHelper {

    public static void bakeClient(final ModConfig config) {
        // Currently no client config
    }

    public static void bakeServer(final ModConfig config) {

        AdvancedEnchantingConfig.baseCost = ConfigHolder.SERVER.baseCost.get();
        AdvancedEnchantingConfig.costFactor = ConfigHolder.SERVER.costFactor.get();
        AdvancedEnchantingConfig.treasureFactor = ConfigHolder.SERVER.treasureFactor.get();
        AdvancedEnchantingConfig.curseFactor = ConfigHolder.SERVER.curseFactor.get();
        AdvancedEnchantingConfig.floatingBookBonus = ConfigHolder.SERVER.floatingBookBonus.get();

        AdvancedEnchantingConfig.blacklistedItems = ConfigHolder.SERVER.blacklistedItems.get();
        AdvancedEnchantingConfig.blacklistedEnchantments =
                ConfigHolder.SERVER.blacklistedEnchantments.get();
    }
}
