package zundubcore.advancedenchanting.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import zundubcore.advancedenchanting.AdvancedEnchanting;

public class PacketHandler extends BasePacketHandler {

    private static final SimpleChannel netHandler = createChannel(
            new ResourceLocation(AdvancedEnchanting.MODID, AdvancedEnchanting.MODID));

    protected SimpleChannel getChannel() {
        return netHandler;
    }

    public void initialize() {
        registerClientToServer(EnchantPacket.class, EnchantPacket::encode, EnchantPacket::decode,
                EnchantPacket::handle);
    }
}
