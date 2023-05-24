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

/** Implements the `Cloud` interface for colored items */
public class CloudItem extends ColoredItem implements Cloud {

	public CloudItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getStorminessTooltip(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		return index == 0 ? this.getStorminessColor(stack) : 0xFFFFFF;
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		this.setStorminessValue(stack, Cloud.MIN_STORMINESS);

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		this.updateStorminess(stack, entity, 1.0D / 40.0D, 1.0D / 80.0D);

		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!NbtHelper.contains(stack, Cloud.STORMINESS_KEY)) {
			this.setStorminessValue(stack, Cloud.MIN_STORMINESS);
		}

		super.onCraft(stack, world, player);
	}
}
