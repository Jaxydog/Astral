package dev.jaxydog.content.item.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.utility.ColorUtil;
import dev.jaxydog.utility.ColorUtil.RGB;
import dev.jaxydog.utility.NbtUtil;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/** Provides utility contants, methods, and implementations for cloudy items */
public interface Cloud {

	/** The NBT key that stores the item's storminess value */
	public static final String STORMINESS_KEY = "Storminess";

	/** The maximum possible item storminess value */
	public static final double MAX_STORMINESS = 1D;
	/** The minimum possible item storminess value */
	public static final double MIN_STORMINESS = 0D;
	/** The numeric margin that determines when the storminess value is rounded */
	public static final double ROUND_THRESHOLD = 0.001D;

	/** The maximum possible item storminess color */
	public static final RGB MAX_COLOR = RGB.from(0xEE_EE_EE);
	/** The minimum possible item storminess color */
	public static final RGB MIN_COLOR = RGB.from(0x66_66_66);

	/** Returns the translation key for the item's storminess tooltip label */
	public static String getStorminessLabelKey() {
		return Astral.getId(Cloud.STORMINESS_KEY.toLowerCase()).toTranslationKey("text");
	}

	/** Returns the text for the item's storminess tooltip label */
	public static MutableText getStorminessLabelText() {
		return Text.translatable(Cloud.getStorminessLabelKey());
	}

	/** Clamps the provided storminess value to fit within the permitted range */
	public static double clampStorminess(double value) {
		if (value < Cloud.MIN_STORMINESS + Cloud.ROUND_THRESHOLD) {
			return Cloud.MIN_STORMINESS;
		}
		if (value > Cloud.MAX_STORMINESS - Cloud.ROUND_THRESHOLD) {
			return Cloud.MAX_STORMINESS;
		}

		return value;
	}

	/** Returns the provided stack's storminess value */
	public default double getStorminess(ItemStack stack) {
		return Cloud.clampStorminess(NbtUtil.getDouble(stack, Cloud.STORMINESS_KEY));
	}

	/** Sets the provided stack's storminess value */
	public default void setStorminess(ItemStack stack, double value) {
		stack.getOrCreateNbt().putDouble(Cloud.STORMINESS_KEY, value);
	}

	/** Returns the provided stack's storminess color */
	public default int getStorminessColor(ItemStack stack) {
		double value = this.getStorminess(stack);
		double range = (Cloud.MAX_STORMINESS - Cloud.MIN_STORMINESS);
		double ratio = (value - Cloud.MIN_STORMINESS) / range;

		return ColorUtil.scaleDown(Cloud.MIN_COLOR, Cloud.MAX_COLOR, ratio).getInt();
	}

	/** Returns the provided stack's storminess tooltip text */
	public default Text getStorminessText(ItemStack stack) {
		MutableText label = Cloud.getStorminessLabelText();
		double value = this.getStorminess(stack) * 100D;
		String percent = String.format(Locale.getDefault(), "%.0f", value);

		return label.append(":").append(ScreenTexts.SPACE).append(percent + "%");
	}

	/** Decreases the provided stack's storminess value */
	public default void decreaseStorminess(ItemStack stack, double delta) {
		double value = this.getStorminess(stack);
		double lerped = MathHelper.lerp(delta, value, Cloud.MIN_STORMINESS);

		this.setStorminess(stack, lerped);
	}

	/** Increases the provided stack's storminess value */
	public default void increaseStorminess(ItemStack stack, double delta) {
		double value = this.getStorminess(stack);
		double lerped = MathHelper.lerp(delta, value, Cloud.MAX_STORMINESS);

		this.setStorminess(stack, lerped);
	}

	/** Updates the item stack's storminess value */
	public default void updateStorminess(ItemStack stack, Entity entity, double increaseDelta,
			double decreaseDelta) {
		double value = this.getStorminess(stack);

		if (entity.isWet() && value < Cloud.MAX_STORMINESS) {
			this.increaseStorminess(stack, increaseDelta);
		} else if (!entity.isWet() && !entity.getWorld().isRaining()
				&& value > Cloud.MIN_STORMINESS) {
			this.decreaseStorminess(stack, decreaseDelta * (entity.isOnFire() ? 2D : 1D));
		}
	}

}
