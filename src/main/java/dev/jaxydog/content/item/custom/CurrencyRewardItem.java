package dev.jaxydog.content.item.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.content.item.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

/** Extends a currency item and implements the currency reward system */
public class CurrencyRewardItem extends CustomItem implements Currency {

	public CurrencyRewardItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the item's default stack with the given identifier */
	public ItemStack getDefaultStack(int id) {
		ItemStack stack = super.getDefaultStack();

		this.setRewardId(stack, id);

		return stack;
	}

	@Override
	public ItemStack getDefaultStack() {
		return this.getDefaultStack(0);
	}

	/** Returns the given stack's reward identifier */
	public int getRewardId(ItemStack stack) {
		return this.getCustomModelData(stack);
	}

	/** Sets the given stack's reward identifier */
	public void setRewardId(ItemStack stack, int id) {
		this.setCustomModelData(stack, id);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "." + this.getCustomModelData(stack);
	}

	@Override
	public void tryCombine(PlayerEntity player, ItemStack stack) {
		final int id = this.getRewardId(stack);
		final Recipe recipe;

		try {
			recipe = Recipe.fromId(id).get();
		} catch (NoSuchElementException _exception) {
			return;
		}

		recipe.tryCraft(player);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot,
			boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (entity instanceof PlayerEntity player) {
			this.tryCombine(player, stack);
		}
	}

	public static final class Recipe {

		/** Stores a list of all skeleton reward recipes */
		private static final ArrayList<Recipe> LIST = new ArrayList<>();

		/** The recipe's skeleton identifier */
		private final int ID;
		/** The recipe's reward ingredients list */
		private final List<Pair<Integer, Integer>> REWARDS;

		public Recipe(int id, List<Pair<Integer, Integer>> rewards) {
			this.ID = id;
			this.REWARDS = rewards;
		}

		/** Returns a list of all skeleton reward recipes */
		public static final List<Recipe> list() {
			return Recipe.LIST;
		}

		/** Returns a list of all skeleton identifiers */
		public static final List<Integer> listIds() {
			return Recipe.list().stream().map(r -> Integer.valueOf(r.getId())).toList();
		}

		/** Returns a set containing all possible reward identifiers */
		public static final Set<Integer> listRewardIds() {
			TreeSet<Integer> set = new TreeSet<>();

			for (Recipe recipe : Recipe.list()) {
				List<Pair<Integer, Integer>> list = recipe.getRewardsList();

				set.addAll(list.stream().map(Pair::getLeft).toList());
			}

			return set;
		}

		/** Returns a recipe with a matching skeleton identifier */
		public static final Optional<Recipe> fromId(int id) {
			return Recipe.list().stream().filter(r -> r.getId() == id).findFirst();
		}

		/** Returns the recipe's skeleton identifier */
		public final int getId() {
			return this.ID;
		}

		/** Returns the recipe's reward ingredients list */
		public final List<Pair<Integer, Integer>> getRewardsList() {
			return this.REWARDS;
		}

		/** Returns whether the given recipe is craftable in the provided inventory */
		public final boolean isCraftableIn(PlayerInventory inventory) {
			for (Pair<Integer, Integer> pair : this.getRewardsList()) {
				final int id = pair.getLeft();
				final int count = pair.getRight();

				int validItems = 0;

				for (int slot = 0; slot < inventory.size(); slot += 1) {
					ItemStack stack = inventory.getStack(slot);

					if (this.validateReward(stack, id)) {
						validItems += stack.getCount();
					}
				}

				if (validItems < count) {
					return false;
				}
			}

			return true;
		}

		public final void tryCraft(PlayerEntity player) {
			final PlayerInventory inventory = player.getInventory();
			final Inventory crafting = player.playerScreenHandler.getCraftingInput();

			while (this.isCraftableIn(inventory)) {
				for (Pair<Integer, Integer> pair : this.getRewardsList()) {
					final int id = pair.getLeft();
					final int count = pair.getRight();

					inventory.remove(stack -> this.validateReward(stack, id), count, crafting);
				}

				ItemStack skeleton = CustomItems.CURRENCY_SKELETON.getDefaultStack(this.getId());

				inventory.offerOrDrop(skeleton);
			}
		}

		/** Returns whether the given stack is a valid reward ingredient */
		public final boolean validateReward(ItemStack stack, int id) {
			if (!(stack.getItem() instanceof CurrencyRewardItem reward)) {
				return false;
			}

			return reward.getCustomModelData(stack) == id;
		}
	}

}
