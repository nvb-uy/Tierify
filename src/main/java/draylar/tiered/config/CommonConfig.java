package draylar.tiered.config;

import java.util.ArrayList;
import java.util.List;

import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.necronomicon.config.Comment;
import elocindev.necronomicon.config.NecConfig;

public class CommonConfig {
    @NecConfig
    public static CommonConfig INSTANCE;

    public static String getFile() {
        return NecConfigAPI.getFile("tierify-common.json5");
    }

    @Comment("Items in for example mineshaft chests get modifiers")
    public boolean lootContainerModifier = true;
    @Comment("Equipped items on entities get modifiers")
    public boolean entityItemModifier = true;
    @Comment("Crafted items get modifiers")
    public boolean craftingModifier = true;
    @Comment("Merchant items get modifiers")
    public boolean merchantModifier = true;
    @Comment("Decreases the biggest weights by this modifier")
    public float reforgeModifier = 0.9F;
    @Comment("Modify the biggest weights by this modifier per smithing level")
    public float levelzReforgeModifier = 0.01F;
    @Comment("Modify the biggest weights by this modifier per luck")
    public float luckReforgeModifier = 0.02F;

    @Comment("Tier 1 of Reforging (Limestone)")
    @Comment("Qualities here will be able to be reforged onto items while using the Tier 1 reforge material, Limestone by default. Can be changed via the item tag tiered:reforge_tier_1")
    public ArrayList<String> tier_1_qualities = new ArrayList<>(
        List.of(
            "Common",
            "Uncommon",
            "Rare"
        )
    );

    @Comment("Tier 2 of Reforging (Pyrite)")
    @Comment("Qualities here will be able to be reforged onto items while using the Tier 2 reforge material, Pyrite by default. Can be changed via the item tag tiered:reforge_tier_2")
    public ArrayList<String> tier_2_qualities = new ArrayList<>(
        List.of(
            "Uncommon",
            "Rare",
            "Epic",
            "Legendary"
        )
    );

    @Comment("Tier 3 of Reforging (Galena)")
    @Comment("Qualities here will be able to be reforged onto items while using the Tier 3 reforge material, Galena by default. Can be changed via the item tag tiered:reforge_tier_3")
    public ArrayList<String> tier_3_qualities = new ArrayList<>(
        List.of(
            "Rare",
            "Epic",
            "Legendary",
            "Mythic"
        )
    );
}
