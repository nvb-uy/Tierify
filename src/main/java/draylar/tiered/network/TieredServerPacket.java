package draylar.tiered.network;

import draylar.tiered.access.AnvilScreenHandlerAccess;
import draylar.tiered.screen.ReforgeScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.libz.network.LibzServerPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class TieredServerPacket {

    public static final Identifier SET_SCREEN = new Identifier("tiered", "set_screen");
    public static final Identifier REFORGE_READY = new Identifier("tiered", "reforge_ready");
    public static final Identifier REFORGE = new Identifier("tiered", "reforge");
    public static final Identifier HEALTH = new Identifier("tiered", "health");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SET_SCREEN, (server, player, handler, buffer, sender) -> {
            int mouseX = buffer.readInt();
            int mouseY = buffer.readInt();
            Boolean reforgingScreen = buffer.readBoolean();
            BlockPos pos = reforgingScreen ? (player.currentScreenHandler instanceof AnvilScreenHandler ? ((AnvilScreenHandlerAccess) player.currentScreenHandler).getPos() : null)
                    : (player.currentScreenHandler instanceof ReforgeScreenHandler ? ((ReforgeScreenHandler) player.currentScreenHandler).getPos() : null);
            if (pos != null) {
                server.execute(() -> {
                    if (reforgingScreen) {
                        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> {
                            return new ReforgeScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(playerx.getWorld(), pos));
                        }, Text.translatable("container.reforge")));
                    } else
                        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> {
                            return new AnvilScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(playerx.getWorld(), pos));
                        }, Text.translatable("container.repair")));

                    LibzServerPacket.writeS2CMousePositionPacket(player, mouseX, mouseY);
                });
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(REFORGE, (server, player, handler, buffer, sender) -> {
            server.execute(() -> {
                if (player.currentScreenHandler instanceof ReforgeScreenHandler)
                    ((ReforgeScreenHandler) player.currentScreenHandler).reforge();
            });
        });
    }

    public static void writeS2CHealthPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(serverPlayerEntity.getHealth());
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(HEALTH, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CReforgeReadyPacket(ServerPlayerEntity serverPlayerEntity, boolean disableButton) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(disableButton);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(REFORGE_READY, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

}
