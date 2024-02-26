package dev.jaxydog.content.item.custom;

import dev.jaxydog.utility.NbtUtil;
import net.minecraft.item.ItemStack;

/** Represents an item that uses custom model data to render */
public interface Customized {

    /** The NBT key that corresponds to the built-in custom model data renderer */
    String CUSTOM_MODEL_DATA_KEY = "CustomModelData";

    /** Returns the stack's custom model identifier */
    default int getCustomModelData(ItemStack stack) {
        return NbtUtil.getInt(stack, CUSTOM_MODEL_DATA_KEY);
    }

    /** Returns the stack's custom model identifier */
    default void setCustomModelData(ItemStack stack, int data) {
        stack.getOrCreateNbt().putInt(CUSTOM_MODEL_DATA_KEY, data);
    }

}
