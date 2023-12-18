package dev.jaxydog.utility;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface AstralItemStack {

	default ItemStack astral$copyWithItemStack(Item item) {
		if (((ItemStack) this).isEmpty()) {
			return ItemStack.EMPTY;
		}

		final ItemStack copy = ((ItemStack) this).copy();

		copy.astral$setItem(item);

		return copy;
	}

	default void astral$setItem(Item item) {}

}
