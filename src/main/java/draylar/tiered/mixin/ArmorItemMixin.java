package draylar.tiered.mixin;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMultimap$Builder;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void initMixin(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings, CallbackInfo info, ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder,
            UUID uUID) {
        if (material != ArmorMaterials.NETHERITE && material.getKnockbackResistance() > 0.0001f) {
            builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                    new EntityAttributeModifier(uUID, "Armor knockback resistance", (double) material.getKnockbackResistance(), EntityAttributeModifier.Operation.ADDITION));
        }
    }
}
