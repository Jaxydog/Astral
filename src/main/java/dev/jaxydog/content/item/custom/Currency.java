package dev.jaxydog.content.item.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import dev.jaxydog.content.item.Customized;
import dev.jaxydog.utility.NbtUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

/** Provides utility constants, methods, and implementations for the currency system */
public interface Currency extends Customized {

	/**
	 * The NBT key that determines whether the given item stack is combinable with other
	 * equally-valued currency stacks
	 */
	public static final String COMBINABLE_KEY = "Combinable";

	/** The list of unit values that are allowed to produce a reward */
	public static final List<Unit> REWARDABLE = List.of(Unit.SKULL);

	/** Returns whether the given item stack may be combined with equally-valued currency stacks */
	public default boolean isCombinable(ItemStack stack) {
		if (NbtUtil.contains(stack, Currency.COMBINABLE_KEY)) {
			return NbtUtil.getBoolean(stack, Currency.COMBINABLE_KEY);
		}

		return true;
	}

	/** Sets whether the given item stack may be combined with equally-valued currency stacks */
	public default void setCombinable(ItemStack stack, boolean isCombinable) {
		stack.getOrCreateNbt().putBoolean(COMBINABLE_KEY, isCombinable);
	}

	/** Returns the given item stack's stored currency unit */
	public default Optional<Unit> getUnit(ItemStack stack) {
		return Unit.fromId(this.getCustomModelData(stack));
	}

	/** Sets the given item stack's stored currency unit */
	public default void setUnit(ItemStack stack, Unit unit) {
		this.setCustomModelData(stack, unit.getId());
	}

	/** Attempts to automatically combine equally-valued currency stacks together */
	public default void tryCombine(PlayerEntity player) {
		final PlayerInventory inventory = player.getInventory();

		for (int slot = 0; slot < inventory.size(); slot += 1) {
			ItemStack stack = inventory.getStack(slot);

			if (stack.getItem() instanceof Currency currency) {
				currency.tryCombine(player, stack);
			}
		}
	}

	/** Attempts to automatically combine equally-valued currency stacks together */
	public void tryCombine(PlayerEntity player, ItemStack stack);

	/** Represents the representable currency units */
	public static final class Unit implements Comparable<Unit> {

		/** Contains all defined units */
		private static final ArrayList<Unit> LIST = new ArrayList<>();

		/** The base unit of currency */
		public static final Unit SHARD = new Unit(1, 1);
		/** The half-bone unit */
		public static final Unit HALF = new Unit(2, Unit.SHARD.getShards() * 4);
		/** The bone unit */
		public static final Unit BONE = new Unit(3, Unit.HALF.getShards() * 2);
		/** The skull unit */
		public static final Unit SKULL = new Unit(4, Unit.BONE.getShards() * 10);
		/** The big-skull unit */
		public static final Unit BIGSKULL = new Unit(5, Unit.SKULL.getShards() * 2);

		/** The unit's identifier */
		private final int ID;
		/** The unit's shard equivalent */
		private final int SHARDS;

		public Unit(int id, int shards) {
			this.ID = id;
			this.SHARDS = shards;

			Unit.LIST.add(this);
			Collections.sort(Unit.LIST);
		}

		/** Returns a list of all defined currency units */
		public static final List<Unit> list() {
			return Unit.LIST;
		}

		/** Returns a unit with a matching identifier */
		public static final Optional<Unit> fromId(int id) {
			return Unit.list().stream().filter(v -> v.getId() == id).findFirst();
		}

		/** Returns a unit with a matching shard equivalent */
		public static final Optional<Unit> fromShards(int shards) {
			return Unit.list().stream().filter(v -> v.getShards() == shards).findFirst();
		}

		/** Returns the unit's identifier */
		public final int getId() {
			return this.ID;
		}

		/** Returns the unit's shard equivalent */
		public final int getShards() {
			return this.SHARDS;
		}

		/** Returns the unit before this one */
		public final Optional<Unit> getLast() {
			int index = Unit.list().indexOf(this);

			if (index > 0) {
				return Optional.ofNullable(Unit.list().get(index - 1));
			} else {
				return Optional.empty();
			}
		}

		/** Returns the unit after this one */
		public final Optional<Unit> getNext() {
			int index = Unit.list().indexOf(this);

			if (index < Unit.list().size() - 1) {
				return Optional.ofNullable(Unit.list().get(index + 1));
			} else {
				return Optional.empty();
			}
		}

		@Override
		public int compareTo(Unit other) {
			return Integer.compare(this.getShards(), other.getShards());
		}

	}

}
