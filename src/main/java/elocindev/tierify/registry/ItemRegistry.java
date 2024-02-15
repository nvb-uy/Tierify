package elocindev.tierify.registry;

import elocindev.tierify.Tierify;
import elocindev.tierify.item.ReforgeAddition;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ItemRegistry {
    
    public static final Item LIMESTONE_CHUNK = register(new ReforgeAddition(new Item.Settings(), 1), "limestone_chunk");
    public static final Item RAW_PYRITE = register(new ReforgeAddition(new Item.Settings(), 2), "pyrite_chunk");
    public static final Item RAW_GALENA = register(new ReforgeAddition(new Item.Settings(), 3), "galena_chunk");

    public static void init() {}

    public static Item register(Item item, String name) {
        return Registry.register(Registries.ITEM, Tierify.id(name), item);
    }
}
