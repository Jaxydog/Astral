package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.ColoredItem;
import dev.jaxydog.astral.utility.NbtHelper;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Implements the `Cloudy` interface for colored items */
public class CloudyItem extends ColoredItem implements Cloudy {

	public CloudyItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getStorminessTooltip(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	protected int getColor(ItemStack stack, int index) {
		return index == 0 ? this.getStorminessColor(stack) : 0xFFFFFF;
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		Cloudy.setStorminess(stack, Cloudy.MIN_VALUE);

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		this.updateStorminess(stack, world, entity);

		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!NbtHelper.contains(stack, Cloudy.STORMINESS_KEY)) {
			Cloudy.setStorminess(stack, Cloudy.MIN_VALUE);
		}

		super.onCraft(stack, world, player);
	}
}
