package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class StrawberryMilkItem extends CustomItem {

	public StrawberryMilkItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
		if (entity instanceof final PlayerEntity player && !player.isCreative()) {
			player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
		}

		final ItemStack usedStack = super.finishUsing(stack, world, entity);

		return stack;
	}

}
