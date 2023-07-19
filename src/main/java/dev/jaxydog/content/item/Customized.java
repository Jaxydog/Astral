package dev.jaxydog.content.item;

import dev.jaxydog.utility.NbtUtil;
import net.minecraft.item.ItemStack;

/** Represents an item that uses custom model data to render */
public interface Customized {

	/** Returns the stack's custom model identifier */
	public default int getCustomModelData(ItemStack stack) {
		return NbtUtil.getInt(stack, NbtUtil.CUSTOM_MODEL_DATA_KEY);
	}

	/** Returns the stack's custom model identifier */
	public default void setCustomModelData(ItemStack stack, int data) {
		stack.getOrCreateNbt().putInt(NbtUtil.CUSTOM_MODEL_DATA_KEY, data);
	}

}
