package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.ColorHelper;
import dev.jaxydog.astral.utility.NbtHelper;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/** Provides common functionality for cloudy items */
public interface Cloudy {
	/** The NBT key that stores an item's storminess value */
	public static final String STORMINESS_KEY = "Storminess";
	/** The minimum possible item storminess color */
	public static final int MIN_COLOR = 0x606060;
	/** The maximum possible item storminess color */
	public static final int MAX_COLOR = 0xEFEFEF;
	/** The maximum possible storminess value */
	public static final double MAX_VALUE = 1.0D;
	/** The minimum possible storminess value */
	public static final double MIN_VALUE = 0.0D;

	/** Returns the given item stack's storminess value */
	public static double getStorminess(ItemStack stack) {
		return NbtHelper.getDouble(stack, Cloudy.STORMINESS_KEY);
	}

	/** Sets the given item stack's storminess value */
	public static void setStorminess(ItemStack stack, double value) {
		stack.getOrCreateNbt().putDouble(Cloudy.STORMINESS_KEY, value);
	}

	/** Clamps the given value to the accepted storminess range */
	public static double clampStorminess(double value) {
		value = Math.max(Cloudy.MIN_VALUE, Math.min(Cloudy.MAX_COLOR, value));

		if (value >= Cloudy.MAX_VALUE - 0.001D) value = Cloudy.MAX_VALUE;
		if (value <= Cloudy.MIN_VALUE + 0.001D) value = Cloudy.MIN_VALUE;

		return value;
	}

	/** Returns the translation key for the item's storminess label */
	public static String getStorminessTranslationKey() {
		return Astral.getId(Cloudy.STORMINESS_KEY).toTranslationKey("text");
	}

	/** Returns the item's storminess color */
	public default int getStorminessColor(ItemStack stack) {
		var value = Cloudy.clampStorminess(Cloudy.getStorminess(stack));
		var ratio = (value - Cloudy.MIN_COLOR) / (Cloudy.MAX_VALUE - Cloudy.MIN_VALUE);

		return ColorHelper.scaleColorDown(Cloudy.MIN_COLOR, Cloudy.MAX_COLOR, (float) ratio);
	}

	/** Returns the item's storminess tooltip */
	public default Text getStorminessTooltip(ItemStack stack) {
		var tag = Text.translatable(Cloudy.getStorminessTranslationKey());
		var value = Cloudy.clampStorminess(Cloudy.getStorminess(stack));
		var string = String.format(Locale.getDefault(), "%.0f", 100.0D * value);

		return tag.append(":").append(ScreenTexts.SPACE).append(string + "%").formatted(Formatting.GRAY);
	}

	/** Decreases the item stack's storminess value */
	public default void decreaseStorminess(ItemStack stack, double value, double delta) {
		var lerped = MathHelper.lerp(delta, value, Cloudy.MAX_VALUE);

		Cloudy.setStorminess(stack, Cloudy.clampStorminess(lerped));
	}

	/** Increases the item stack's storminess value */
	public default void increaseStorminess(ItemStack stack, double value, double delta) {
		var lerped = MathHelper.lerp(delta, value, Cloudy.MIN_VALUE);

		Cloudy.setStorminess(stack, Cloudy.clampStorminess(lerped));
	}

	/** Updates the item stack's storminess value */
	public default void updateStorminess(
		ItemStack stack,
		World world,
		Entity entity,
		double increaseDelta,
		double decreaseDelta
	) {
		var value = Cloudy.clampStorminess(Cloudy.getStorminess(stack));

		if (entity.isWet() && value < Cloudy.MAX_VALUE) {
			this.increaseStorminess(stack, value, increaseDelta);
		} else if (!entity.isWet() && !world.isRaining() && value > Cloudy.MIN_VALUE) {
			this.decreaseStorminess(stack, value, decreaseDelta);
		}
	}

	/** Updates the item stack's storminess value */
	public default void updateStorminess(ItemStack stack, World world, Entity entity) {
		this.updateStorminess(stack, world, entity, 0.75D, 0.25D);
	}
}
