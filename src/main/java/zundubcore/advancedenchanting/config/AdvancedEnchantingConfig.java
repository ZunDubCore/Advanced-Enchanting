package zundubcore.advancedenchanting.config;

import java.util.List;

/**
 * This holds the baked (runtime) values for our config. These values should never be from changed
 * outside this package. This can be split into multiple classes (Server, Client, Player, Common)
 * but has been kept in one class for simplicity
 */
public final class AdvancedEnchantingConfig {

    // Client
    // Currently no client config

    // Server
    public static int baseCost;
    public static double costFactor;
    public static double treasureFactor;
    public static double curseFactor;
    public static double floatingBookBonus;

    public static List<String> blacklistedItems;
    public static List<String> blacklistedEnchantments;
}
