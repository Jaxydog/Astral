package dev.jaxydog.astral_additions.content.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

/** Implements the currency reward skeleton crafting system */
public class CurrencySkeletonItem extends CurrencyItem {

	public CurrencySkeletonItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public boolean attemptAutoCraft(ItemStack stack, Random random, PlayerEntity player) {
		return false;
	}

	@Override
	public ItemStack getDefaultStack() {
		return this.getCustomStack(0);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.getTranslationKey() + "." + Currency.getStackId(stack);
	}
}
