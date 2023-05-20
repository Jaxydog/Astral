package dev.jaxydog.astral_additions.content.item.custom;

import dev.jaxydog.astral_additions.utility.NbtHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

/** Provides utility constants, methods, and implementations for the currency item system */
public interface Currency {
	/** The NBT key that toggles automatic crafting of currency items */
	public static final String AUTO_CRAFTABLE_KEY = "AutoCraftable";
	/** The chance of gaining a rewards item whilst auto-crafting */
	public static final float REWARD_CHANCE = 0.1f;
	/** The currency values that are allowed to produce a reward when auto-crafting */
	public static final List<Currency.Value> REWARDED_VALUES = List.of(Currency.Value.SKULL);

	/** Returns whether the given item stack can be used in auto-crafting */
	public static boolean isAutoCraftable(ItemStack stack) {
		if (NbtHelper.contains(stack, Currency.AUTO_CRAFTABLE_KEY)) {
			return NbtHelper.getBoolean(stack, Currency.AUTO_CRAFTABLE_KEY);
		} else {
			return true;
		}
	}

	/** Sets whether the given item stack can be used in auto-crafting */
	public static void setAutoCraftable(ItemStack stack, boolean enabled) {
		stack.getOrCreateNbt().putBoolean(Currency.AUTO_CRAFTABLE_KEY, enabled);
	}

	/** Returns the given item stack's custom identifier */
	public static int getStackId(ItemStack stack) {
		return NbtHelper.getInt(stack, NbtHelper.CUSTOM_MODEL_DATA_KEY);
	}

	/** Sets the given item stack's custom identifier */
	public static void setStackId(ItemStack stack, int id) {
		stack.getOrCreateNbt().putInt(NbtHelper.CUSTOM_MODEL_DATA_KEY, id);
	}

	/** Sets the given item stack's custom identifier */
	public static void setStackId(ItemStack stack, Currency.Value value) {
		Currency.setStackId(stack, value.getId());
	}

	/** Ease-of-use method that checks whether the provided stack is a valid, possibly autocraftable, currency item */
	public static boolean matchStack(ItemStack stack, int id, boolean requireAuto) {
		if (!(stack.getItem() instanceof Currency)) return false;
		if (Currency.getStackId(stack) != id) return false;
		if (requireAuto && !Currency.isAutoCraftable(stack)) return false;

		return true;
	}

	/** Ease-of-use method that checks whether the provided stack is a valid, possibly autocraftable, currency item */
	public static boolean matchStack(ItemStack stack, Currency.Value value, boolean requireAuto) {
		return Currency.matchStack(stack, value.getId(), requireAuto);
	}

	/** Returns the number of generated rewards that the current auto-craft should provide */
	public static int getRewardCount(Currency.Value value, int totalCrafted, Random random) {
		if (!Currency.REWARDED_VALUES.contains(value)) return 0;

		var rewards = 0;

		for (var _index = 0; _index < totalCrafted; _index += 1) {
			if (random.nextFloat() > Currency.REWARD_CHANCE) continue;
			rewards += 1;
		}

		return rewards;
	}

	/** Attempts to automatically craft with the given item stack */
	public boolean attemptAutoCraft(ItemStack stack, Random random, PlayerEntity player);

	/** Represents the possible types of currency items that an item stack could represent */
	public static enum Value {
		SHARD(1, 1),
		HALF(Value.SHARD.getId() + 1, Value.SHARD.getShards() * 4),
		BONE(Value.HALF.getId() + 1, Value.HALF.getShards() * 2),
		SKULL(Value.BONE.getId() + 1, Value.BONE.getShards() * 10),
		BIG_SKULL(Value.SKULL.getId() + 1, Value.SKULL.getShards() * 2);

		/** The value's custom model identifier */
		private final int ID;
		/** The value's shard representation */
		private final int SHARDS;
		/** The currency value before this one */
		private final int LAST;
		/** The currency value after this one */
		private final int NEXT;

		private Value(int id, int shards) {
			this.ID = id;
			this.SHARDS = shards;
			this.LAST = id - 1;
			this.NEXT = id + 1;
		}

		/** Returns a value with an identifier equal to the provided identifier */
		public static Optional<Value> from(int id) {
			for (var value : Value.values()) {
				if (value.getId() == id) return Optional.of(value);
			}

			return Optional.empty();
		}

		/** Returns the value's custom model identifier */
		public final int getId() {
			return this.ID;
		}

		/** Returns the number of shards that make up one of this value */
		public final int getShards() {
			return this.SHARDS;
		}

		/** Returns the currency value before this one */
		public final Optional<Value> getLast() {
			return Value.from(this.LAST);
		}

		/** Returns the currency value after this one */
		public final Optional<Value> getNext() {
			return Value.from(this.NEXT);
		}
	}
}
