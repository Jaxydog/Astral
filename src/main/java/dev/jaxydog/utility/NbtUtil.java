package dev.jaxydog.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import java.util.Optional;

/** Provides utility methods to safely interact with an item stack's NBT data */
@NonExtendable
public interface NbtUtil {

	/** Returns an optional NBT element from the given item stack */
	static Optional<NbtElement> get(ItemStack stack, String key) {
		return NbtUtil.getCompound(stack).map(nbt -> nbt.get(key));
	}

	/** Returns the base NBT compound stored in the item stack */
	static Optional<NbtCompound> getCompound(ItemStack stack) {
		return Optional.ofNullable(stack.getNbt());
	}

	/** Returns the NBT type of the element stored at the provided key */
	static Optional<Byte> getType(ItemStack stack, String key) {
		return NbtUtil.getCompound(stack).map(nbt -> nbt.getType(key));
	}

	/** Returns `true` if the item stack contains the provided NBT key */
	static boolean contains(ItemStack stack, String key) {
		return stack.hasNbt() && stack.getNbt().contains(key);
	}

	/** Returns `true` if the item stack contains the provided NBT key with the given type */
	static boolean contains(ItemStack stack, String key, int type) {
		return stack.hasNbt() && stack.getNbt().contains(key, type);
	}

	/** Returns the boolean stored at the provided key, defaulted to `false` */
	static boolean getBoolean(ItemStack stack, String key) {
		return NbtUtil.getByte(stack, key) != 0;
	}

	/** Returns the byte stored at the provided key, defaulted to `0` */
	static byte getByte(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getByte(key) : 0;
	}

	/** Returns the short stored at the provided key, defaulted to `0` */
	static short getShort(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getShort(key) : 0;
	}

	/** Returns the int stored at the provided key, defaulted to `0` */
	static int getInt(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getInt(key) : 0;
	}

	/** Returns the long stored at the provided key, defaulted to `0` */
	static long getLong(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getLong(key) : 0;
	}

	/** Returns the float stored at the provided key, defaulted to `0` */
	static float getFloat(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getFloat(key) : 0;
	}

	/** Returns the double stored at the provided key, defaulted to `0` */
	static double getDouble(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getDouble(key) : 0;
	}

	/** Returns the string stored at the provided key, defaulted to `""` */
	static String getString(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getString(key) : "";
	}

	/** Returns the byte array stored at the provided key, defaulted to `[]` */
	static byte[] getByteArray(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getByteArray(key) : new byte[0];
	}

	/** Returns the int array stored at the provided key, defaulted to `[]` */
	static int[] getIntArray(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getIntArray(key) : new int[0];
	}

	/** Returns the long array stored at the provided key, defaulted to `[]` */
	static long[] getLongArray(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getLongArray(key) : new long[0];
	}

	/** Returns the NBT compound stored at the provided key, defaulted to an empty compound */
	static NbtCompound getCompound(ItemStack stack, String key) {
		return stack.hasNbt() ? stack.getNbt().getCompound(key) : new NbtCompound();
	}

	/** Returns the NBT list stored at the provided key, defaulted to an empty list */
	static NbtList getList(ItemStack stack, String key, int type) {
		return stack.hasNbt() ? stack.getNbt().getList(key, type) : new NbtList();
	}

}
