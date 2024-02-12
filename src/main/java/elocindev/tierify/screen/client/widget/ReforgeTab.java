package elocindev.tierify.screen.client.widget;

import org.jetbrains.annotations.Nullable;

import elocindev.tierify.network.TieredClientPacket;
import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ReforgeTab extends InventoryTab {

    public ReforgeTab(Text title, @Nullable Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public void onClick(MinecraftClient client) {
        TieredClientPacket.writeC2SScreenPacket((int) client.mouse.getX(), (int) client.mouse.getY(), true);
    }

}
