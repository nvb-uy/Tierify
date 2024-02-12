package elocindev.tierify.mixin.client;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import elocindev.tierify.TierifyClient;
import elocindev.tierify.config.ConfigInit;
import elocindev.tierify.util.TieredTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    public HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void drawMouseoverTooltipMixin(DrawContext context, int x, int y, CallbackInfo info, ItemStack stack) {
        if (ConfigInit.CLIENT_CONFIG.tieredTooltip && stack.hasNbt() && stack.getNbt().contains("Tiered")) {

            String nbtString = stack.getNbt().getCompound("Tiered").asString();
            for (int i = 0; i < TierifyClient.BORDER_TEMPLATES.size(); i++) {
                if (!TierifyClient.BORDER_TEMPLATES.get(i).containsStack(stack) && TierifyClient.BORDER_TEMPLATES.get(i).containsDecider(nbtString)) {
                    TierifyClient.BORDER_TEMPLATES.get(i).addStack(stack);
                } else if (TierifyClient.BORDER_TEMPLATES.get(i).containsStack(stack)) {
                    List<Text> text = Screen.getTooltipFromItem(client, stack);

                    List<TooltipComponent> list = text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
                    stack.getTooltipData().ifPresent(data -> list.add(1, TooltipComponent.of(data)));

                    TieredTooltip.renderTieredTooltipFromComponents(context, this.textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, TierifyClient.BORDER_TEMPLATES.get(i));

                    info.cancel();
                    break;
                }
            }
        }
    }

}
