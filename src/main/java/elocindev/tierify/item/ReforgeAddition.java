package elocindev.tierify.item;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import elocindev.tierify.Tierify;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class ReforgeAddition extends Item {
    private int tier = -1;

    public ReforgeAddition(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }
    
    public int getTier() {
        return tier;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ArrayList<String> qualities = new ArrayList<String>();

        switch(getTier()) {
            case 1:
                qualities = Tierify.CONFIG.tier_1_qualities;
                break;
            case 2:
                qualities = Tierify.CONFIG.tier_2_qualities;
                break;
            case 3:
                qualities = Tierify.CONFIG.tier_3_qualities;
                break;
        }

        if (qualities.size() == 0) return;
        
        
        tooltip.add(Text.literal("Reforging Qualities:").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        for (String quality : qualities) {
            MutableText separator = Text.literal(" - ").setStyle(Style.EMPTY.withColor(Formatting.GRAY));

            tooltip.add(separator.append(Text.literal(quality).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY))));
        }
    }
}
