package dev.jaxydog.content.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class CurrencySkeletonItem extends CurrencyRewardItem {

	public CurrencySkeletonItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public void tryCombine(PlayerEntity player) {}

	@Override
	public void tryCombine(PlayerEntity player, ItemStack stack) {}

}
