package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class CloudyItem extends CustomItem implements Cloudy, Colored {

    private static final double INCREASE_DELTA = 1D / 80D;
    private static final double DECREASE_DELTA = 1D / 160D;

    public CloudyItem(String rawId, Settings settings) {
        super(rawId, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(this.getStorminessText(stack));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public int getColor(ItemStack stack, int index) {
        return index == 0 ? this.getStorminessColor(stack) : 0xFF_FF_FF;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        this.updateStorminess(stack, entity, INCREASE_DELTA, DECREASE_DELTA);

        super.inventoryTick(stack, world, entity, slot, selected);
    }

}
