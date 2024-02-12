package draylar.tiered.util;

import java.util.List;

import draylar.tiered.api.BorderTemplate;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;

public class TieredTooltip {
    
    @Deprecated()
    public static void renderTieredTooltipFromComponents(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, BorderTemplate borderTemplate) {
        elocindev.tierify.util.TieredTooltip.renderTieredTooltipFromComponents(context, textRenderer, components, x, y, positioner, borderTemplate);
    }

}
