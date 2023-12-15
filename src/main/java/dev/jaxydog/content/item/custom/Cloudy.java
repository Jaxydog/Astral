package dev.jaxydog.content.item.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.utility.ColorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/** Provides constants, methods, and implementations for cloudy items. */
public interface Cloudy {

	/** The NBT key used to determine and store an item stack's storminess value. */
	public static final String STORMINESS_KEY = "Storminess";
	/** The translation key assigned to the item's storminess tooltip label. */
	public static final String STORMINESS_LABEL_KEY = Astral.getId(Cloudy.STORMINESS_KEY.toLowerCase())
		.toTranslationKey("text");

	/** The minimum possible item color value. */
	public static final ColorUtil.RGB COLOR_MIN = ColorUtil.RGB.from(0x66_66_66);
	/** The maximum possible item color value. */
	public static final ColorUtil.RGB COLOR_MAX = ColorUtil.RGB.from(0xEE_EE_EE);

	/** Returns this item's minimum storminess value. */
	public default double getMinStorminess(ItemStack stack) {
		return 0D;
	}

	/** Returns this item's maximum storminess value. */
	public default double getMaxStorminess(ItemStack stack) {
		return 1D;
	}

	/** Clamps the provided storminess value within the permitted range. */
	public default double clampStorminess(ItemStack stack, double storminess) {
		final double min = this.getMinStorminess(stack);
		final double max = this.getMaxStorminess(stack);
		final double margin = (max - min) / 1000D;

		if (storminess <= min + margin) {
			return min;
		}
		if (storminess >= max - margin) {
			return max;
		}

		return storminess;
	}

	/** Returns the given item stack's storminess value. */
	public default double getStorminess(ItemStack stack) {
		final double storminess;

		if (stack.hasNbt() && stack.getNbt().contains(Cloudy.STORMINESS_KEY)) {
			storminess = stack.getNbt().getDouble(Cloudy.STORMINESS_KEY);
		} else {
			storminess = this.getMinStorminess(stack);
		}

		return this.clampStorminess(stack, storminess);
	}

	/** Sets the given item stack's storminess value. */
	public default void setStorminess(ItemStack stack, double storminess) {
		final double min = this.getMinStorminess(stack);

		storminess = this.clampStorminess(stack, storminess);

		if (storminess <= min + Math.ulp(storminess)) {
			stack.removeSubNbt(Cloudy.STORMINESS_KEY);
		} else {
			stack.getOrCreateNbt().putDouble(Cloudy.STORMINESS_KEY, storminess);
		}
	}

	/** Returns the given item stack's storminess color. */
	public default int getStorminessColor(ItemStack stack) {
		final double storminess = this.getStorminess(stack);
		final double min = this.getMinStorminess(stack);
		final double max = this.getMaxStorminess(stack);
		final double percentage = (storminess - min) / (max - min);

		return ColorUtil.scaleDown(Cloudy.COLOR_MIN, Cloudy.COLOR_MAX, percentage).getInt();
	}

	/** Returns the given item stack's storminess tooltip text. */
	public default Text getStorminessText(ItemStack stack) {
		final MutableText label = Text.translatable(Cloudy.STORMINESS_LABEL_KEY);
		final double storminess = this.getStorminess(stack) * 100D;
		final String percentage = String.format("%.0f%%", storminess);

		return label.append(": ").append(percentage);
	}

	/**
	 * Increases the given item stack's storminess value towards its maximum value by the given delta.
	 */
	public default void increaseStorminess(ItemStack stack, double delta) {
		final double storminess = this.getStorminess(stack);
		final double max = this.getMaxStorminess(stack);

		this.setStorminess(stack, MathHelper.lerp(delta, storminess, max));
	}

	/**
	 * Decreases the given item stack's storminess value towards its maximum value by the given delta.
	 */
	public default void decreaseStorminess(ItemStack stack, double delta) {
		final double storminess = this.getStorminess(stack);
		final double min = this.getMinStorminess(stack);

		this.setStorminess(stack, MathHelper.lerp(delta, storminess, min));
	}

	/**
	 * Updates the item stack's storminess value depending on environmental conditions.
	 */
	public default boolean updateStorminess(ItemStack stack, Entity entity, double increaseDelta,
		double decreaseDelta) {
		final double storminess = this.getStorminess(stack);

		if (entity.isTouchingWater()) {
			this.increaseStorminess(stack, increaseDelta);
		} else if (entity.isWet()) {
			this.increaseStorminess(stack, increaseDelta / 2D);
		} else if (entity.isOnFire() || entity.isInLava()) {
			this.decreaseStorminess(stack, decreaseDelta);
		} else if (!entity.getWorld().isRaining()) {
			this.decreaseStorminess(stack, decreaseDelta / 2D);
		}

		return this.getStorminess(stack) != storminess;
	}

}
