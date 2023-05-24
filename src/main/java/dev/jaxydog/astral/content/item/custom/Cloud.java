package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.ColorUtil;
import dev.jaxydog.astral.utility.NbtHelper;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/** Provides common functionality for cloud items */
public interface Cloud {
	/** The NBT key that stores the item's storminess value */
	public static final String STORMINESS_KEY = "Storminess";
	/** The maximum possible item storminess value */
	public static final double MAX_STORMINESS = 1.0D;
	/** The minimum possible item storminess value */
	public static final double MIN_STORMINESS = 0.0D;
	/** The margin of error before the storminess value is rounded */
	public static final double ROUND_THRESHOLD = 0.001D;

	/** The maximum possible item storminess color */
	public static final ColorUtil.RGB MAX_COLOR = ColorUtil.RGB.from(0xEEEEEE);
	/** The minimum possible item storminess color */
	public static final ColorUtil.RGB MIN_COLOR = ColorUtil.RGB.from(0x666666);

	/** Returns the translation key for the item's storminess tooltip label */
	public static String getStorminessTranslationKey() {
		return Astral.getId(Cloud.STORMINESS_KEY.toLowerCase()).toTranslationKey("text");
	}

	/** Normalizes the provided storminess value to fit within the allowed range */
	public static double normalizeStorminessValue(double value) {
		if (value < Cloud.MIN_STORMINESS + Cloud.ROUND_THRESHOLD) return Cloud.MIN_STORMINESS;
		if (value > Cloud.MAX_STORMINESS - Cloud.ROUND_THRESHOLD) return Cloud.MAX_STORMINESS;

		return value;
	}

	/** Returns the given stack's storminess value */
	public default double getStorminessValue(ItemStack stack) {
		return Cloud.normalizeStorminessValue(NbtHelper.getDouble(stack, Cloud.STORMINESS_KEY));
	}

	/** Sets the given item stack's storminess value */
	public default void setStorminessValue(ItemStack stack, double value) {
		stack.getOrCreateNbt().putDouble(Cloud.STORMINESS_KEY, Cloud.normalizeStorminessValue(value));
	}

	/** Returns the given item stack's storminess color */
	public default int getStorminessColor(ItemStack stack) {
		var value = this.getStorminessValue(stack);
		var ratio = (value - Cloud.MIN_STORMINESS) / (Cloud.MAX_STORMINESS - Cloud.MIN_STORMINESS);

		return ColorUtil.scaleDown(Cloud.MIN_COLOR, Cloud.MAX_COLOR, (float) ratio).intoInt();
	}

	/** Returns the given item stack's storminess tooltip */
	public default Text getStorminessTooltip(ItemStack stack) {
		var tag = Text.translatable(Cloud.getStorminessTranslationKey());
		var value = this.getStorminessValue(stack) * 100.0D;
		var string = String.format(Locale.getDefault(), "%.0f", value);

		return tag.append(":").append(ScreenTexts.SPACE).append(string + "%");
	}

	/** Decreases the given item stack's storminess value */
	public default void decreaseStorminess(ItemStack stack, double delta) {
		var value = this.getStorminessValue(stack);
		var lerped = MathHelper.lerp(delta, value, Cloud.MIN_STORMINESS);

		this.setStorminessValue(stack, lerped);
	}

	/** Increases the given item stack's storminess value */
	public default void increaseStorminess(ItemStack stack, double delta) {
		var value = this.getStorminessValue(stack);
		var lerped = MathHelper.lerp(delta, value, Cloud.MAX_STORMINESS);

		this.setStorminessValue(stack, lerped);
	}

	/** Updates the item stack's storminess value */
	public default void updateStorminess(ItemStack stack, Entity entity, double increaseDelta, double decreaseDelta) {
		var value = this.getStorminessValue(stack);

		if (entity.isWet() && value < Cloud.MAX_STORMINESS) {
			this.increaseStorminess(stack, increaseDelta);
		} else if (!entity.isWet() && !entity.getWorld().isRaining() && value > Cloud.MIN_STORMINESS) {
			this.decreaseStorminess(stack, decreaseDelta);
		}
	}
}
