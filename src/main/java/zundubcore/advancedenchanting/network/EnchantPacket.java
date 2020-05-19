package zundubcore.advancedenchanting.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

import zundubcore.advancedenchanting.container.AdvancedEnchantingTableContainer;

public class EnchantPacket {

    public EnchantPacket() {}

    public static void handle(EnchantPacket message, Supplier<Context> context) {

        final AdvancedEnchantingTableContainer container =
                (AdvancedEnchantingTableContainer) context.get().getSender().openContainer;

        // TODO: accept enchantment id or any argument
        container.enchantItem();

        context.get().setPacketHandled(true);
    }

    public static void encode(EnchantPacket pkt, PacketBuffer buf) {}

    public static EnchantPacket decode(PacketBuffer buf) {
        return new EnchantPacket();
    }
}
