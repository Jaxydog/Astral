package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.ColorUtil;
import dev.jaxydog.astral.utility.ColorUtil.Rgb;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/** Provides constants, methods, and implementations for cloudy items. */
public interface Cloudy {

    /** The NBT key used to determine and store an item stack's storminess value. */
    String STORMINESS_KEY = "Storminess";
    /** The translation key assigned to the item's storminess tooltip label. */
    String STORMINESS_LABEL_KEY = Astral.getId(STORMINESS_KEY.toLowerCase()).toTranslationKey("text");

    /** The minimum possible item color value. */
    Rgb COLOR_MIN = new Rgb(0x66_66_66);
    /** The maximum possible item color value. */
    Rgb COLOR_MAX = new Rgb(0xEE_EE_EE);

    /** Sets the given item stack's storminess value. */
    default void setStorminess(ItemStack stack, double storminess) {
        final double min = this.getMinStorminess(stack);

        storminess = this.clampStorminess(stack, storminess);

        if (storminess <= min + Math.ulp(storminess)) {
            stack.removeSubNbt(STORMINESS_KEY);
        } else {
            stack.getOrCreateNbt().putDouble(STORMINESS_KEY, storminess);
        }
    }

    /** Returns the given item stack's storminess color. */
    default int getStorminessColor(ItemStack stack) {
        final double storminess = this.getStorminess(stack);
        final double min = this.getMinStorminess(stack);
        final double max = this.getMaxStorminess(stack);
        final double percentage = (storminess - min) / (max - min);

        return ColorUtil.transition(COLOR_MIN, COLOR_MAX, 1D - percentage).asInt();
    }

    /** Returns the given item stack's storminess value. */
    default double getStorminess(ItemStack stack) {
        final double storminess;
        final NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(STORMINESS_KEY)) {
            storminess = nbt.getDouble(STORMINESS_KEY);
        } else {
            storminess = this.getMinStorminess(stack);
        }

        return this.clampStorminess(stack, storminess);
    }

    /** Returns this item's minimum storminess value. */
    @SuppressWarnings("SameReturnValue")
    default double getMinStorminess(ItemStack stack) {
        return 0D;
    }

    /** Returns this item's maximum storminess value. */
    @SuppressWarnings("SameReturnValue")
    default double getMaxStorminess(ItemStack stack) {
        return 1D;
    }

    /** Clamps the provided storminess value within the permitted range. */
    default double clampStorminess(ItemStack stack, double storminess) {
        final double min = this.getMinStorminess(stack);
        final double max = this.getMaxStorminess(stack);
        // Allow for a small margin of error before clamping.
        final double margin = ((max - min) / 1000D) + Math.ulp(storminess);

        if (storminess <= min + margin) return min;
        if (storminess >= max - margin) return max;

        return storminess;
    }

    /** Returns the given item stack's storminess tooltip text. */
    default Text getStorminessText(ItemStack stack) {
        final MutableText label = Text.translatable(STORMINESS_LABEL_KEY);
        final double storminess = this.getStorminess(stack) * 100D;
        final String percentage = String.format("%.0f%%", storminess);

        return label.append(": ").append(percentage);
    }

    /**
     * Increases the given item stack's storminess value towards its maximum value by the given delta.
     */
    default void increaseStorminess(ItemStack stack, double delta) {
        final double storminess = this.getStorminess(stack);
        final double max = this.getMaxStorminess(stack);

        this.setStorminess(stack, MathHelper.lerp(delta, storminess, max));
    }

    /**
     * Decreases the given item stack's storminess value towards its maximum value by the given delta.
     */
    default void decreaseStorminess(ItemStack stack, double delta) {
        final double storminess = this.getStorminess(stack);
        final double min = this.getMinStorminess(stack);

        this.setStorminess(stack, MathHelper.lerp(delta, storminess, min));
    }

    /**
     * Updates the item stack's storminess value depending on environmental conditions.
     */
    default void updateStorminess(
        ItemStack stack, Entity entity, double increaseDelta, double decreaseDelta
    ) {
        if (entity.isTouchingWater()) {
            this.increaseStorminess(stack, increaseDelta);
        } else if (entity.isWet()) {
            this.increaseStorminess(stack, increaseDelta / 2D);
        } else if (entity.isOnFire() || entity.isInLava()) {
            this.decreaseStorminess(stack, decreaseDelta);
        } else if (!entity.getWorld().isRaining()) {
            this.decreaseStorminess(stack, decreaseDelta / 2D);
        }
    }

}
