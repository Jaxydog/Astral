package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.color.ColoredItem;
import dev.jaxydog.utility.NbtUtil;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Implements the 'Cloud' interface for colored items */
public class CloudItem extends ColoredItem implements Cloud {

	public CloudItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip,
			TooltipContext context) {
		tooltip.add(this.getStorminessText(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		return index == 0 ? this.getStorminessColor(stack) : 0xFF_FF_FF;
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = super.getDefaultStack();

		this.setStorminess(stack, Cloud.MIN_STORMINESS);

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot,
			boolean selected) {
		this.updateStorminess(stack, entity, 1D / 40D, 1D / 80D);

		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!NbtUtil.contains(stack, Cloud.STORMINESS_KEY)) {
			this.setStorminess(stack, this.getStorminess(this.getDefaultStack()));
		}

		super.onCraft(stack, world, player);
	}

}
