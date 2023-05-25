package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.ColoredArmorItem;
import dev.jaxydog.astral.utility.NbtUtil;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Implements the `Cloud` interface for colored armor items */
public class CloudArmorItem extends ColoredArmorItem implements Cloud, DyeableItem {

	public CloudArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(rawId, material, type, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getStorminessTooltip(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		if (index == 0) return this.getColor(stack);
		if (index == 1) return this.getStorminessColor(stack);

		return 0xFFFFFF;
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		this.setStorminessValue(stack, Cloud.MIN_STORMINESS);
		this.setColor(stack, DyeableItem.DEFAULT_COLOR);

		return stack;
	}

	@Override
	public int getTextureLayers(ItemStack stack) {
		return 2;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		this.updateStorminess(stack, entity, 1.0D / 160.0D, 1.0D / 320.0D);

		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!NbtUtil.contains(stack, Cloud.STORMINESS_KEY)) {
			this.setStorminessValue(stack, Cloud.MIN_STORMINESS);
		}

		super.onCraft(stack, world, player);
	}
}
