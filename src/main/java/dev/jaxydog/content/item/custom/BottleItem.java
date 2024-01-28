package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BottleItem extends CustomItem {

	public BottleItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		return ItemUsage.consumeHeldItem(world, player, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
		final @Nullable PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

		if (player instanceof final ServerPlayerEntity serverPlayer) {
			Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
		}

		if (player != null) {
			player.incrementStat(Stats.USED.getOrCreateStat(this));

			if (!player.isCreative()) stack.decrement(1);
		}

		if (player == null || !player.isCreative()) {
			if (stack.isEmpty()) return Items.GLASS_BOTTLE.getDefaultStack();

			if (player != null) player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
		}

		entity.emitGameEvent(GameEvent.DRINK);

		return stack;
	}

}
