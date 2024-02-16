package elocindev.tierify.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundRegistry {

    public static SoundEvent REFORGE_SOUND_COMMON = register("reforge_sound_common");
    public static SoundEvent REFORGE_SOUND_UNCOMMON = register("reforge_sound_uncommon");
    public static SoundEvent REFORGE_SOUND_RARE = register("reforge_sound_rare");
    public static SoundEvent REFORGE_SOUND_EPIC = register("reforge_sound_epic");
    public static SoundEvent REFORGE_SOUND_LEGENDARY = register("reforge_sound_legendary");
    public static SoundEvent REFORGE_SOUND_MYTHIC = register("reforge_sound_mythic");


    public static void registerSounds() {
    }

    private static SoundEvent register(String name) {
        Identifier id = new Identifier("tiered", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

}
