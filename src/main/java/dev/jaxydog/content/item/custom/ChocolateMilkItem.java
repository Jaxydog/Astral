package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.Objects;

import static net.minecraft.item.SuspiciousStewItem.forEachEffect;

public class ChocolateMilkItem extends CustomItem {

	public ChocolateMilkItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof final PlayerEntity player && !player.isCreative()) {
			player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
		}
		ItemStack itemStack = super.finishUsing(stack, world, user);
		if (user != null) {
			forEachEffect(itemStack, user::addStatusEffect);
		}
		return stack;
	}

}
