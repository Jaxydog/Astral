package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.CustomItems;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

/** Currency item extension to be used as a reward for using the system */
public class CurrencyRewardItem extends CurrencyItem {

	/** Stores matching reward sets for skeleton crafting */
	public final HashMap<Integer, List<Integer>> ITEM_SETS = new HashMap<>();

	public CurrencyRewardItem(String rawId, Settings settings, Map<Integer, List<Integer>> sets) {
		super(rawId, settings);
		this.ITEM_SETS.putAll(sets);
	}

	@Override
	public boolean attemptAutoCraft(ItemStack stack, Random random, PlayerEntity player) {
		var rewardId = Currency.getStackId(stack);

		var optionalSkeletonId = this.getContainingSetId(rewardId);
		if (optionalSkeletonId.isEmpty()) return false;

		var inventory = player.getInventory();
		var skeletonId = optionalSkeletonId.get();
		var rewardList = this.ITEM_SETS.get(skeletonId);
		var totalCrafted = Integer.MAX_VALUE;

		for (var id : rewardList) {
			var contains = false;

			for (var slot = 0; slot < inventory.size(); slot += 1) {
				var inventoryStack = inventory.getStack(slot);

				if (!Currency.matchStack(inventoryStack, id, true)) continue;

				totalCrafted = Math.min(totalCrafted, inventoryStack.getCount());
				contains = true;
			}

			if (!contains) return false;
		}

		if (totalCrafted <= 0 || totalCrafted == Integer.MAX_VALUE) return false;

		var skeletonStack = CustomItems.CURRENCY_SKELETON.getCustomStack(skeletonId);

		skeletonStack.setCount(totalCrafted);
		inventory.offerOrDrop(skeletonStack);

		var craftingInventory = player.playerScreenHandler.getCraftingInput();

		for (var id : rewardList) {
			inventory.remove(s -> Currency.matchStack(s, id, true), totalCrafted, craftingInventory);
		}

		return true;
	}

	/** Returns the given reward identifier's parent set identifier */
	public Optional<Integer> getContainingSetId(int id) {
		for (var entry : this.ITEM_SETS.entrySet()) {
			if (entry.getValue().contains(id)) return Optional.of(entry.getKey());
		}

		return Optional.empty();
	}

	@Override
	public ItemStack getDefaultStack() {
		return this.getCustomStack(0);
	}

	/** Returns a list of possible reward identifiers */
	public Set<Integer> getPossibleRewardIds() {
		var set = new HashSet<Integer>();

		for (var list : this.ITEM_SETS.values()) list.addAll(list);

		return set;
	}

	/** Returns an item stack with a randomly-assigned identifier */
	public ItemStack getRandomStack(Random random) {
		var ids = this.getPossibleRewardIds();

		if (ids.size() == 0) return this.getDefaultStack();

		var index = random.nextBetween(0, ids.size() - 1);
		var stack = this.getCustomStack((int) ids.toArray()[index]);

		return stack;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.getTranslationKey() + "." + Currency.getStackId(stack);
	}
}
