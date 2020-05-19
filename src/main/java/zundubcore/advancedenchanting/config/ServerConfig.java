package zundubcore.advancedenchanting.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

import zundubcore.advancedenchanting.AdvancedEnchanting;

/**
 * For configuration settings that change the behaviour of code on the LOGICAL SERVER. This can be
 * moved to an inner class of ExampleModConfig, but is separate because of personal preference and
 * to keep the code organised
 */
final class ServerConfig {

    final ForgeConfigSpec.IntValue baseCost;
    final ForgeConfigSpec.DoubleValue costFactor;
    final ForgeConfigSpec.DoubleValue treasureFactor;
    final ForgeConfigSpec.DoubleValue curseFactor;
    final ForgeConfigSpec.DoubleValue floatingBookBonus;

    final ForgeConfigSpec.ConfigValue<List<String>> blacklistedItems;
    final ForgeConfigSpec.ConfigValue<List<String>> blacklistedEnchantments;

    ServerConfig(final ForgeConfigSpec.Builder builder) {

        builder.push("general");

        baseCost = builder
                .comment("The base cost to use for the enchantment formula. ")
                .translation(AdvancedEnchanting.MODID + ".config.serverBaseCost")
                .defineInRange("serverBaseCost", 45, 1, 1024);

        costFactor = builder
                .comment("A number used when calculated enchantment cost. This number is treated "
                        + "as a % based factor. 0.30 = 30% of the original cost. 1.5 = 150% of "
                        + "the original cost.")
                .translation(AdvancedEnchanting.MODID + ".config.serverCostFactor")
                .defineInRange("serverCostFactor", 1d, 0d, 1024d);

        treasureFactor = builder
                .comment("A factor used to make treasure enchantments like mending cost more to "
                        + "apply. By default they cost 4X more.")
                .translation(AdvancedEnchanting.MODID + ".config.serverTreasureFactor")
                .defineInRange("serverTreasureFactor", 4d, 0d, 1024d);

        curseFactor = builder
                .comment("A factor used to make curse enchantments like vanishing cost more to "
                        + "apply. By default they cost 3X more.")
                .translation(AdvancedEnchanting.MODID + ".config.serverCurseFactor")
                .defineInRange("serverCurseFactor", 3d, 0d, 1024d);

        floatingBookBonus = builder
                .comment("The amount of enchantment power a floating book should give. Bookshelfs "
                        + "have 1 power.")
                .translation(AdvancedEnchanting.MODID + ".config.serverFloatingBookBonus")
                .defineInRange("serverFloatingBookBonus", 1d, 0d, 1024d);

        builder.pop();

        builder.push("blacklist");

        blacklistedItems = builder
                .comment("A blacklist of items that can't be enchanted with this mod. Format is "
                        + "itemid#meta")
                .translation(AdvancedEnchanting.MODID + ".config.serverBlacklistedItems")
                .define("serverBlacklistedItems", new ArrayList<>());

        blacklistedEnchantments = builder
                .comment("A blacklist of enchantments that are not available in E+. Format is just "
                        + "enchantmentid.")
                .translation(AdvancedEnchanting.MODID + ".config.serverBlacklistedEnchantments")
                .define("serverBlacklistedEnchantments", new ArrayList<>());

        builder.pop();
    }

}
