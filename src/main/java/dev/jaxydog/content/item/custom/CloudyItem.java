package dev.jaxydog.content.item.custom;

import java.util.List;
import dev.jaxydog.content.item.color.ColoredItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class CloudyItem extends ColoredItem implements Cloudy {

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
		this.updateStorminess(stack, entity, CloudyItem.INCREASE_DELTA, CloudyItem.DECREASE_DELTA);

		super.inventoryTick(stack, world, entity, slot, selected);
	}

}
