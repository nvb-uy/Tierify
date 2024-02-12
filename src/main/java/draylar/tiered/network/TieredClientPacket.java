package draylar.tiered.network;

import draylar.tiered.client.ReforgeScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

@Environment(EnvType.CLIENT)
public class TieredClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(TieredServerPacket.REFORGE_READY, (client, handler, buf, sender) -> {
            boolean disableButton = buf.readBoolean();
            client.execute(() -> {
                if (client.currentScreen instanceof ReforgeScreen)
                    ((ReforgeScreen) client.currentScreen).reforgeButton.setDisabled(disableButton);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(TieredServerPacket.HEALTH, (client, handler, buf, sender) -> {
            float health = buf.readFloat();
            client.execute(() -> {
                client.player.setHealth(health);
            });
        });
    }

    public static void writeC2SScreenPacket(int mouseX, int mouseY, boolean reforgingScreen) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(mouseX);
        buf.writeInt(mouseY);
        buf.writeBoolean(reforgingScreen);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(TieredServerPacket.SET_SCREEN, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

    public static void writeC2SReforgePacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(TieredServerPacket.REFORGE, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

}
