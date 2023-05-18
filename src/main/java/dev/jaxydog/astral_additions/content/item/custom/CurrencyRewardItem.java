package dev.jaxydog.astral_additions.content.item.custom;

import dev.jaxydog.astral_additions.content.item.CustomItem;
import dev.jaxydog.astral_additions.content.item.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** Represents a possible reward from using currency */
public class CurrencyRewardItem extends CustomItem {

	/** The NBT key that determines the item's model */
	public static final String REWARD_TYPE_KEY = "RewardType";
	/** The maximum model identifier, used in stack generation */
	public static final int MAX_TYPE_ID = 3;
	/** Contains matching sets of reward items for skeleton crafting */
	public static final int[][] ITEM_SETS = new int[][] {
		{}, // this should always be left empty, as it represents skeleton `0`, which does not have a model
	};

	public CurrencyRewardItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the given reward identifier's containing set */
	private static @Nullable int[] getRewardSet(int type) {
		for (var set : CurrencyRewardItem.ITEM_SETS) {
			for (var id : set) {
				if (id == type) return set;
			}
		}

		return null;
	}

	/** Returns the given reward set's skeleton identifier */
	private static int getRewardSetId(int[] set) {
		for (var index = 1; index < CurrencyRewardItem.ITEM_SETS.length; index += 1) {
			if (CurrencyRewardItem.ITEM_SETS[index] == set) return index;
		}

		return 0;
	}

	/** Returns the item stack's reward type from its NBT data */
	public static final int getRewardType(ItemStack stack) {
		var nbt = stack.getNbt();

		if (nbt == null || !nbt.contains(CurrencyRewardItem.REWARD_TYPE_KEY)) return 0;

		return nbt.getInt(CurrencyRewardItem.REWARD_TYPE_KEY);
	}

	/** Sets the given item stack's reward type via NBT data */
	public static final void setRewardType(ItemStack stack, int type) {
		var nbt = stack.getOrCreateNbt();
		nbt.putInt(CurrencyRewardItem.REWARD_TYPE_KEY, type);
		nbt.putInt("CustomModelData", type);
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		CurrencyRewardItem.setRewardType(stack, 0);

		return stack;
	}

	/** Returns an item stack with a randomly-assigned type identifier */
	public ItemStack getRandomStack(Random random) {
		var stack = this.getDefaultStack();
		var type = random.nextBetween(1, CurrencyRewardItem.MAX_TYPE_ID);

		CurrencyRewardItem.setRewardType(stack, type);

		return stack;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		var type = CurrencyRewardItem.getRewardType(stack);
		var key = super.getTranslationKey(stack);

		return key + "." + type;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (!(entity instanceof PlayerEntity)) return;

		var type = CurrencyRewardItem.getRewardType(stack);
		var set = CurrencyRewardItem.getRewardSet(type);

		if (set == null) return;

		var player = (PlayerEntity) entity;
		var inventory = player.getInventory();

		for (var id : set) if (!inventory.containsAny(s -> CurrencyRewardItem.getRewardType(s) == id)) return;

		var crafting = player.playerScreenHandler.getCraftingInput();
		var setIndex = CurrencyRewardItem.getRewardSetId(set);
		var crafted = CustomItems.CURRENCY_SKELETON.getDefaultStack();

		CurrencySkeletonItem.setSkeletonType(crafted, setIndex);

		if (!player.giveItemStack(crafted)) player.dropStack(crafted);

		for (var id : set) inventory.remove(s -> CurrencyRewardItem.getRewardType(s) == id, 1, crafting);
	}
}
