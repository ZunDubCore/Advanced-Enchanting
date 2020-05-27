package zundubcore.advancedenchanting.config;

import net.minecraftforge.common.ForgeConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

/**
 * This holds the Client &amp; Server Configs and the Client &amp; Server ConfigSpecs. It can be
 * merged into the main ExampleModConfig class, but is separate because of personal preference and
 * to keep the code organised
 */
public final class ConfigHolder {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;

    static final ClientConfig CLIENT;
    static final ServerConfig SERVER;

    static {
        {
            final Pair<ClientConfig, ForgeConfigSpec> specPair =
                    new ForgeConfigSpec.Builder().configure(ClientConfig::new);

            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            final Pair<ServerConfig, ForgeConfigSpec> specPair =
                    new ForgeConfigSpec.Builder().configure(ServerConfig::new);

            SERVER = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
}
