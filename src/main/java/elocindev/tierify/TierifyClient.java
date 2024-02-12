package elocindev.tierify;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.libz.registry.TabRegistry;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import draylar.tiered.api.BorderTemplate;
import draylar.tiered.api.PotentialAttribute;
import elocindev.tierify.data.AttributeDataLoader;
import elocindev.tierify.data.TooltipBorderLoader;
import elocindev.tierify.network.TieredClientPacket;
import elocindev.tierify.screen.ReforgeScreenHandler;
import elocindev.tierify.screen.client.ReforgeScreen;
import elocindev.tierify.screen.client.widget.AnvilTab;
import elocindev.tierify.screen.client.widget.ReforgeTab;

@Environment(EnvType.CLIENT)
public class TierifyClient implements ClientModInitializer {

    // map for storing attributes before logging into a server
    public static final Map<Identifier, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();

    public static final List<BorderTemplate> BORDER_TEMPLATES = new ArrayList<BorderTemplate>();

    private static final Identifier ANVIL_TAB_ICON = new Identifier("tiered:textures/gui/anvil_tab_icon.png");
    private static final Identifier REFORGE_TAB_ICON = new Identifier("tiered:textures/gui/reforge_tab_icon.png");

    public static final boolean isBCLibLoaded = FabricLoader.getInstance().isModLoaded("bclib");

    @Override
    public void onInitializeClient() {
        registerAttributeSyncHandler();
        registerReforgeItemSyncHandler();
        HandledScreens.<ReforgeScreenHandler, ReforgeScreen>register(Tierify.REFORGE_SCREEN_HANDLER_TYPE, ReforgeScreen::new);
        TieredClientPacket.init();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new TooltipBorderLoader());
        TabRegistry.registerOtherTab(new AnvilTab(Text.translatable("container.repair"), ANVIL_TAB_ICON, 0, AnvilScreen.class), AnvilScreen.class);
        TabRegistry.registerOtherTab(new ReforgeTab(Text.translatable("screen.tiered.reforging_screen"), REFORGE_TAB_ICON, 1, ReforgeScreen.class), AnvilScreen.class);
    }

    public static void registerAttributeSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Tierify.ATTRIBUTE_SYNC_PACKET, (client, play, packet, packetSender) -> {
            // save old attributes
            CACHED_ATTRIBUTES.putAll(Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes());
            Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().clear();

            // for each id/attribute pair, load it
            int size = packet.readInt();
            for (int i = 0; i < size; i++) {
                Identifier id = new Identifier(packet.readString());
                PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(packet.readString(), PotentialAttribute.class);
                Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().put(id, pa);
            }
        });
    }

    public static void registerReforgeItemSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Tierify.REFORGE_ITEM_SYNC_PACKET, (client, play, packet, packetSender) -> {
            List<Identifier> identifiers = new ArrayList<Identifier>();
            List<List<Integer>> list = new ArrayList<List<Integer>>();
            while (packet.isReadable()) {
                int count = packet.readInt();
                identifiers.add(packet.readIdentifier());
                List<Integer> idList = new ArrayList<Integer>();
                for (int i = 0; i < count; i++) {
                    idList.add(packet.readInt());
                }
                list.add(idList);
            }
            client.execute(() -> {
                Tierify.REFORGE_DATA_LOADER.clearReforgeBaseItems();
                for (int i = 0; i < identifiers.size(); i++) {
                    List<Item> items = new ArrayList<Item>();
                    for (int u = 0; u < list.get(i).size(); u++) {
                        items.add(Registries.ITEM.get(list.get(i).get(u)));
                    }
                    Tierify.REFORGE_DATA_LOADER.putReforgeBaseItems(identifiers.get(i), items);
                }
            });
        });
    }
}
