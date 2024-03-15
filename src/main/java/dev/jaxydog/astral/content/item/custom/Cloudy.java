/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023–2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.ColorUtil;
import dev.jaxydog.astral.utility.ColorUtil.Rgb;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Provides constants, methods, and implementations for cloudy items.
 * <p>
 * This interface handles most of the actual code required for an item's storminess value being updated automatically.
 * It is required, however, for manual invocation of the {@link #updateStorminess(ItemStack, Entity)} method, as well as
 * new implementations for the update delta provider methods.
 *
 * @author Jaxydog
 * @since 1.4.0
 */
public interface Cloudy {

    /**
     * The NBT key used to determine and store an item stack's storminess value.
     *
     * @since 1.4.0
     */
    String STORMINESS_KEY = "Storminess";
    /**
     * The translation key assigned to the item's storminess tooltip label.
     * <p>
     * This will be the equivalent of {@code "text.astral.storminess"}
     *
     * @since 1.4.0
     */
    String STORMINESS_LABEL_KEY = Astral.getId(STORMINESS_KEY.toLowerCase()).toTranslationKey("text");

    /**
     * The minimum possible item color value.
     *
     * @since 1.4.0
     */
    Rgb COLOR_MIN = new Rgb(0x66_66_66);
    /**
     * The maximum possible item color value.
     *
     * @since 1.4.0
     */
    Rgb COLOR_MAX = new Rgb(0xEE_EE_EE);

    /**
     * Sets the given item stack's storminess value.
     *
     * @param stack The item stack.
     * @param storminess The storminess level.
     *
     * @since 1.4.0
     */
    default void setStorminess(ItemStack stack, double storminess) {
        // Ensure the supplied value fits within the expected bounds.
        storminess = this.clampStorminess(stack, storminess);

        // Remove the item's NBT tag if the storminess is roughly at the minimum bound.
        // This prevents the annoying "feature" of cloudy items, where they would no longer be stackable once they
        // have any level of storminess.
        if (storminess <= (this.getMinStorminess(stack) + Math.ulp(storminess))) {
            stack.removeSubNbt(STORMINESS_KEY);
        } else {
            stack.getOrCreateNbt().putDouble(STORMINESS_KEY, storminess);
        }
    }

    /**
     * Returns the given item stack's storminess color.
     *
     * @param stack The item stack.
     *
     * @return The sRGB color as an integer.
     *
     * @since 1.4.0
     */
    default int getStorminessColor(ItemStack stack) {
        final double min = this.getMinStorminess(stack);
        final double max = this.getMaxStorminess(stack);
        final double storminess = this.getStorminess(stack);

        // The minimum and maximum values must be used to get the percentage, as just checking against `max` would
        // consider values below `min` to be acceptable. This calculation will properly consider values below `min`
        // to have a negative percentage of fulfillment.
        final double percentage = (storminess - min) / (max - min);

        // We subtract percentage from `1D` because we want to transition *away* from `COLOR_MAX`, down towards
        // `COLOR_MIN`. This also means, however, if the percentage is < 0D || > 1D, the color will be outside the
        // expected range.
        // Perhaps in the future a clamp will be added to prevent erroneous values.
        return ColorUtil.transition(COLOR_MIN, COLOR_MAX, 1D - percentage).asInt();
    }

    /**
     * Returns the given item stack's storminess value.
     *
     * @param stack The item stack.
     *
     * @return The storminess value.
     *
     * @since 1.4.0
     */
    default double getStorminess(ItemStack stack) {
        final NbtCompound nbt = stack.getNbt();

        // If the NBT value is not present, we can assume that the value is at the minimum bound.
        if (nbt != null && nbt.contains(STORMINESS_KEY)) {
            final double storminess = nbt.getDouble(STORMINESS_KEY);

            // Ensure that the returned value is within the allowed range, since the NBT may be set to an invalid value.
            return this.clampStorminess(stack, storminess);
        } else {
            // No need to clamp this, since it's always a valid value.
            return this.getMinStorminess(stack);
        }
    }

    /**
     * Returns this item's minimum storminess value for the given item stack.
     *
     * @param stack The item stack.
     *
     * @return The lowest possible value.
     *
     * @since 1.4.0
     */
    default double getMinStorminess(ItemStack stack) {
        return 0D;
    }

    /**
     * Returns this item's maximum storminess value for the given item stack.
     *
     * @param stack The item stack.
     *
     * @return The highest possible value.
     *
     * @since 1.4.0
     */
    default double getMaxStorminess(ItemStack stack) {
        return 1D;
    }

    /**
     * Clamps the provided storminess value within the permitted range for the given item stack.
     *
     * @param stack The item stack.
     * @param storminess The storminess value.
     *
     * @return The clamped storminess value.
     *
     * @since 1.4.0
     */
    default double clampStorminess(ItemStack stack, double storminess) {
        final double min = this.getMinStorminess(stack);
        final double max = this.getMaxStorminess(stack);
        // Allow for a small margin of error before clamping.
        // This margin is effectively 1/1000th of the allowed range +/- the smallest possible variation for this double.
        final double margin = ((max - min) / 1000D) + Math.ulp(storminess);

        // Consider `min` to be slightly above the actual minimum.
        if (storminess <= min + margin) return min;
        // Consider `max` to be slightly below the actual maximum.
        if (storminess >= max - margin) return max;

        return storminess;
    }

    /**
     * Returns the given item stack's storminess tooltip text.
     *
     * @param stack The item stack.
     *
     * @return The tooltip text.
     *
     * @since 1.4.0
     */
    default Text getStorminessText(ItemStack stack) {
        final double min = this.getMinStorminess(stack);
        final double max = this.getMaxStorminess(stack);
        final double storminess = this.getStorminess(stack);
        // The minimum and maximum values must be used to get the percentage, as just checking against `max` would
        // consider values below `min` to be acceptable. This calculation will properly consider values below `min`
        // to have a negative percentage of fulfillment.
        //
        // We also need to multiply by 100, otherwise the percentage will be within the range of 0-1.
        final double percentage = ((storminess - min) / (max - min)) * 100D;

        // The base label is simply determined using the label translation key constant.
        // This will equate to something like 'Storminess: 50.0%'.
        return Text.translatable(STORMINESS_LABEL_KEY).append(": %.0f%%".formatted(storminess));
    }

    /**
     * Returns the given item stack's storminess increase delta.
     * <p>
     * This controls the rate at which a given item's storminess value is increased.
     *
     * @param stack The item stack.
     *
     * @return The increase delta.
     *
     * @since 2.0.0
     */
    double getIncreaseDelta(ItemStack stack);

    /**
     * Returns the given item stack's storminess decrease delta.
     * <p>
     * This controls the rate at which a given item's storminess value is decreased.
     *
     * @param stack The item stack.
     *
     * @return The decrease delta.
     *
     * @since 2.0.0
     */
    double getDecreaseDelta(ItemStack stack);

    /**
     * Increases the given item stack's storminess value towards its maximum value by the given delta.
     *
     * @param stack The item stack.
     * @param delta The amount of change.
     *
     * @since 1.4.0
     */
    default void increaseStorminess(ItemStack stack, double delta) {
        final double storminess = this.getStorminess(stack);
        final double max = this.getMaxStorminess(stack);

        // Storminess is linearly interpolated to provide a transitional "color animation", with the provided delta
        // controlling the speed of the interpolation.
        this.setStorminess(stack, MathHelper.lerp(delta, storminess, max));
    }

    /**
     * Decreases the given item stack's storminess value towards its maximum value by the given delta.
     *
     * @param stack The item stack.
     * @param delta The amount of change.
     *
     * @since 1.4.0
     */
    default void decreaseStorminess(ItemStack stack, double delta) {
        final double storminess = this.getStorminess(stack);
        final double min = this.getMinStorminess(stack);

        // Storminess is linearly interpolated to provide a transitional "color animation", with the provided delta
        // controlling the speed of the interpolation.
        this.setStorminess(stack, MathHelper.lerp(delta, storminess, min));
    }

    /**
     * Updates the item stack's storminess value depending on environmental conditions.
     *
     * @param stack The item stack.
     * @param entity The entity holding the stack.
     * @param increaseDelta The delta used in {@link #increaseStorminess(ItemStack, double)}.
     * @param decreaseDelta The delta used in {@link #decreaseStorminess(ItemStack, double)}.
     *
     * @since 1.4.0
     */
    default void updateStorminess(ItemStack stack, Entity entity, double increaseDelta, double decreaseDelta) {
        // Increase normally if submerged in water.
        if (entity.isSubmergedInWater()) {
            this.increaseStorminess(stack, increaseDelta);
        }
        // Increase at half of the rate if otherwise wet.
        else if (entity.isWet()) {
            this.increaseStorminess(stack, increaseDelta / 2D);
        }
        // Decrease normally if on fire or in lava.
        else if (entity.isOnFire() || entity.isInLava()) {
            this.decreaseStorminess(stack, decreaseDelta);
        }
        // Decrease at half of the rate if not in water or rain (this is usually true at this point).
        else if (!entity.isTouchingWaterOrRain()) {
            this.decreaseStorminess(stack, decreaseDelta / 2D);
        }
    }

    /**
     * Updates the item stack's storminess value depending on environmental conditions.
     * <p>
     * This simply invokes the overloaded {@link #updateStorminess(ItemStack, Entity, double, double)} method, with
     * delta values determined by this value's {@link #getIncreaseDelta(ItemStack)} and
     * {@link #getDecreaseDelta(ItemStack)} methods.
     *
     * @param stack The item stack.
     * @param entity The entity holding the stack.
     *
     * @since 2.0.0
     */
    default void updateStorminess(ItemStack stack, Entity entity) {
        final double increase = this.getIncreaseDelta(stack);
        final double decrease = this.getDecreaseDelta(stack);

        this.updateStorminess(stack, entity, increase, decrease);
    }

}
