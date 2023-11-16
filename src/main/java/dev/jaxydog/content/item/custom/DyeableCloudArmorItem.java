package dev.jaxydog.content.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Implements the 'DyeableItem' interface for cloud armor items */
public class DyeableCloudArmorItem extends CloudArmorItem implements DyeableItem {

	public DyeableCloudArmorItem(String rawId, ArmorMaterial material, Type type,
			Settings settings) {
		super(rawId, material, type, settings);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		if (index == 0)
			return this.getColor(stack);
		if (index == 1)
			return this.getStorminessColor(stack);

		return 0xFF_FF_FF;
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		this.setColor(stack, DyeableItem.DEFAULT_COLOR);

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot,
			boolean selected) {
		this.updateStorminess(stack, entity, 1.0D / 160.0D, 1.0D / 320.0D);
	}

}
