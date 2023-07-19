package dev.jaxydog.content.item.custom;

import java.util.NoSuchElementException;
import java.util.Set;
import dev.jaxydog.content.CustomGamerules;
import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.content.item.CustomItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/** Extends a custom item and implements the basic currency system */
public class CurrencyItem extends CustomItem implements Currency {

	public CurrencyItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the item's default stack with the given currency value and combinable setting */
	public ItemStack getDefaultStack(Unit unit, boolean isCombinable) {
		ItemStack stack = new ItemStack(this);

		this.setUnit(stack, unit);
		this.setCombinable(stack, isCombinable);

		return stack;
	}

	@Override
	public ItemStack getDefaultStack() {
		return this.getDefaultStack(Unit.SHARD, true);
	}

	/** Returns the total number of generated rewards for the current combine process */
	public int getTotalRewards(Random random, Unit unit, int totalCrafted, double chance) {
		if (!Currency.REWARDABLE.contains(unit)) {
			return 0;
		}

		int totalRewards = 0;

		for (int _index = 0; _index < totalCrafted; _index += 1) {
			if (random.nextDouble() < chance) {
				totalRewards += 1;
			}
		}

		return totalRewards;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		int id = this.getUnit(stack).map(Unit::getId)
				.orElseGet(() -> this.getCustomModelData(stack));

		return super.getTranslationKey(stack) + "." + id;
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		this.setCombinable(stack, false);

		super.onCraft(stack, world, player);
	}

	@Override
	public void tryCombine(PlayerEntity player, ItemStack stack) {
		final Unit unit, next;

		try {
			unit = this.getUnit(stack).get();
			next = unit.getNext().get();
		} catch (NoSuchElementException _exception) {
			return;
		}

		final PlayerInventory inventory = player.getInventory();
		final int costForOne = next.getShards() / unit.getShards();

		int validItems = 0;

		for (int slot = 0; slot < inventory.size(); slot += 1) {
			ItemStack content = inventory.getStack(slot);

			if (this.validateStack(content, unit)) {
				validItems += content.getCount();
			}
		}

		if (validItems < costForOne) {
			return;
		}

		final Random random = player.getRandom();
		final GameRules gamerules = player.getWorld().getGameRules();
		final double rewardChance = gamerules.get(CustomGamerules.CURRENCY_REWARD_CHANCE).get();

		final int totalCrafted = validItems / costForOne;
		final int totalRewards = this.getTotalRewards(random, unit, totalCrafted, rewardChance);
		final int costForAll = totalCrafted * costForOne;

		for (int remaining = totalCrafted; remaining > 0; remaining -= this.getMaxCount()) {
			ItemStack combined = this.getDefaultStack(next, true);

			combined.setCount(Math.min(remaining, combined.getMaxCount()));
			inventory.offerOrDrop(combined);
		}

		for (int remaining = totalRewards; remaining > 0; remaining -= 1) {
			Set<Integer> ids = CurrencyRewardItem.Recipe.listRewardIds();
			int id = ((Integer[]) ids.toArray())[random.nextInt(ids.size())];
			ItemStack reward = CustomItems.CURRENCY_REWARD.getDefaultStack(id);

			inventory.offerOrDrop(reward);
		}

		final Inventory crafting = player.playerScreenHandler.getCraftingInput();

		inventory.remove(content -> this.validateStack(content, unit), costForAll, crafting);
	}

	/** Returns whether the given item stack is a valid combinable currency of the given unit */
	private boolean validateStack(ItemStack stack, Unit unit) {
		if (!(stack.getItem() instanceof Currency currency)) {
			return false;
		}
		if (!currency.isCombinable(stack)) {
			return false;
		}
		if (currency.getUnit(stack).filter(u -> u == unit).isEmpty()) {
			return false;
		}

		return true;
	}

}
