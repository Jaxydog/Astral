package dev.jaxydog.content.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.world.World;

public class ChocolateMilkItem extends BottleItem {

	public ChocolateMilkItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
		if (entity != null && !world.isClient()) {
			SuspiciousStewItem.forEachEffect(stack, entity::addStatusEffect);
		}

		return super.finishUsing(stack, world, entity);
	}

}
