package draylar.tiered.util;

import net.minecraft.entity.player.PlayerEntity;

public class AttributeHelper {
    
    @Deprecated()
    public static boolean shouldMeeleCrit(PlayerEntity playerEntity) {
        return elocindev.tierify.util.AttributeHelper.shouldMeeleCrit(playerEntity);
    }

    @Deprecated()
    public static float getExtraDigSpeed(PlayerEntity playerEntity, float oldDigSpeed) {
        return elocindev.tierify.util.AttributeHelper.getExtraDigSpeed(playerEntity, oldDigSpeed);
    }

    @Deprecated()
    public static float getExtraRangeDamage(PlayerEntity playerEntity, float oldDamage) {
        return elocindev.tierify.util.AttributeHelper.getExtraRangeDamage(playerEntity, oldDamage);
    }

    @Deprecated()
    public static float getExtraCritDamage(PlayerEntity playerEntity, float oldDamage) {
        return elocindev.tierify.util.AttributeHelper.getExtraCritDamage(playerEntity, oldDamage);
    }

}
