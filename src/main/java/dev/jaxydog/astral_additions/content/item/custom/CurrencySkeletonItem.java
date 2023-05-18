package dev.jaxydog.astral_additions.content.item.custom;

import dev.jaxydog.astral_additions.content.item.CustomItem;
import net.minecraft.item.ItemStack;

/** Implements the currency system's "skeleton crafting" system */
public class CurrencySkeletonItem extends CustomItem {

	/** The NBT key that determines the item's model */
	public static final String SKELETON_TYPE_KEY = "SkeletonType";

	public CurrencySkeletonItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the item stack's skeleton type from its NBT data */
	public static final int getSkeletonType(ItemStack stack) {
		var nbt = stack.getNbt();

		if (nbt == null || !nbt.contains(CurrencySkeletonItem.SKELETON_TYPE_KEY)) return 0;

		return nbt.getInt(CurrencySkeletonItem.SKELETON_TYPE_KEY);
	}

	/** Sets the given item stack's skeleton type via NBT data */
	public static final void setSkeletonType(ItemStack stack, int type) {
		var nbt = stack.getOrCreateNbt();
		nbt.putInt(CurrencySkeletonItem.SKELETON_TYPE_KEY, type);
		nbt.putInt("CustomModelData", type);
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		CurrencySkeletonItem.setSkeletonType(stack, 0);

		return stack;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		var type = CurrencySkeletonItem.getSkeletonType(stack);
		var key = super.getTranslationKey(stack);

		return key + "." + type;
	}
}
