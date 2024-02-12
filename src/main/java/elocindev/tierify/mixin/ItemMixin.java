package elocindev.tierify.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import draylar.tiered.api.ModifierUtils;
import elocindev.tierify.Tierify;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(Item.class)
public class ItemMixin {

    // onCraft in ItemStack class does get called too and get called in CraftingResultSlot
    // but is air at onTakeItem in CraftingResultSlot when quick crafting is used
    @Inject(method = "onCraft", at = @At("TAIL"))
    private void onCraftMixin(ItemStack stack, World world, PlayerEntity player, CallbackInfo info) {
        if (!world.isClient() && !stack.isEmpty() && Tierify.CONFIG.craftingModifier) {
            ModifierUtils.setItemStackAttribute(player, stack, false);
        }
    }

    @Inject(method = "getItemBarStep", at = @At("HEAD"), cancellable = true)
    private void getItemBarStepMixin(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        if (stack.hasNbt() && stack.getNbt().contains("durable")) {
            info.setReturnValue(Math.round(13.0f - (float) stack.getDamage() * 13.0f / (float) stack.getMaxDamage()));
        }
    }

    @Inject(method = "getItemBarColor", at = @At("HEAD"), cancellable = true)
    private void getItemBarColorMixin(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        if (stack.hasNbt() && stack.getNbt().contains("durable")) {
            float f = Math.max(0.0f, ((float) stack.getMaxDamage() - (float) stack.getDamage()) / (float) stack.getMaxDamage());
            info.setReturnValue(MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f));
        }
    }

}
