package zundubcore.advancedenchanting.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import zundubcore.advancedenchanting.AdvancedEnchanting;

public abstract class BasePacketHandler {

    private int index = 0;

    protected static SimpleChannel createChannel(ResourceLocation name) {

        return NetworkRegistry.ChannelBuilder.named(name).clientAcceptedVersions(
                getProtocolVersion()::equals).serverAcceptedVersions(
                getProtocolVersion()::equals).networkProtocolVersion(
                BasePacketHandler::getProtocolVersion).simpleChannel();
    }

    private static String getProtocolVersion() {
        return AdvancedEnchanting.instance == null ? "999.999.999"
                : AdvancedEnchanting.instance.version;
    }

    /**
     * Helper for reading strings to make sure we don't accidentally call PacketBuffer#readString on
     * the server
     */
    public static String readString(PacketBuffer buffer) {
        // TODO: Evaluate usages and potentially move some things to more strict string
        // length checks
        return buffer.readString(Short.MAX_VALUE);
    }

    protected abstract SimpleChannel getChannel();

    public abstract void initialize();

    protected <MSG> void registerClientToServer(Class<MSG> type,
            BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
            BiConsumer<MSG, Supplier<Context>> consumer) {

        getChannel().registerMessage(index++, type, encoder, decoder, consumer,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    protected <MSG> void registerServerToClient(Class<MSG> type,
            BiConsumer<MSG, PacketBuffer> encoder,
            Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {

        getChannel().registerMessage(index++, type, encoder, decoder, consumer,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    /**
     * Send this message to the specified player.
     *
     * @param message - the message to send
     * @param player  - the player to send it to
     */
    public <MSG> void sendTo(MSG message, ServerPlayerEntity player) {

        getChannel().sendTo(message, player.connection.getNetworkManager(),
                NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Send this message to everyone connected to the server.
     *
     * @param message - message to send
     */
    public <MSG> void sendToAll(MSG message) {
        getChannel().send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send this message to everyone within the supplied dimension.
     *
     * @param message   - the message to send
     * @param dimension - the dimension to target
     */
    public <MSG> void sendToDimension(MSG message, DimensionType dimension) {
        getChannel().send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    /**
     * Send this message to the server.
     *
     * @param message - the message to send
     */
    public <MSG> void sendToServer(MSG message) {
        getChannel().sendToServer(message);
    }
}
