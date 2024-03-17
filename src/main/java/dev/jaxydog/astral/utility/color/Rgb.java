/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.utility.color;

import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * Stores and allows for the manipulation of an RGB value.
 *
 * @author Jaxydog
 * @since 2.0.0
 */
public class Rgb {

    /**
     * A mask for an sRGB color's alpha value.
     *
     * @since 2.0.0
     */
    public static int ALPHA_MASK = 0xFF_00_00_00;
    /**
     * A mask for an sRGB color's red value.
     *
     * @since 2.0.0
     */
    public static int RED_MASK = 0x00_FF_00_00;
    /**
     * A mask for an sRGB color's green value.
     *
     * @since 2.0.0
     */
    public static int GREEN_MASK = 0x00_00_FF_00;
    /**
     * A mask for an sRGB color's blue value.
     *
     * @since 2.0.0
     */
    public static int BLUE_MASK = 0x00_00_00_FF;

    /**
     * The color's red component.
     *
     * @since 2.0.0
     */
    private final byte r;
    /**
     * The color's green component.
     *
     * @since 2.0.0
     */
    private final byte g;
    /**
     * The color's blue component.
     *
     * @since 2.0.0
     */
    private final byte b;

    /**
     * The color, converted into the HSV format.
     *
     * @since 2.0.0
     */
    private float[] hsb = null;

    /**
     * Creates a new RGB value.
     * <p>
     * The provided values are expected to be within a range of {@code [0, 255]}. If a value exceeds this, it will be
     * clamped.
     *
     * @param red The red component.
     * @param green The green component.
     * @param blue The blue component.
     *
     * @since 2.0.0
     */
    public Rgb(int red, int green, int blue) {
        this.r = (byte) (MathHelper.clamp(red, 0, 255));
        this.g = (byte) (MathHelper.clamp(green, 0, 255));
        this.b = (byte) (MathHelper.clamp(blue, 0, 255));
    }

    /**
     * Creates a new RGB value.
     * <p>
     * The provided values are expected to be within a range of {@code [0, 1]}. If a value exceed this, it will be
     * clamped.
     *
     * @param red The red component.
     * @param green The green component.
     * @param blue The blue component.
     *
     * @since 2.0.0
     */
    public Rgb(double red, double green, double blue) {
        // Clamp between 0 and 1, then scale up to [0, 255].
        final int r = (int) (MathHelper.clamp(red, 0D, 1D) * 255D);
        final int g = (int) (MathHelper.clamp(green, 0D, 1D) * 255D);
        final int b = (int) (MathHelper.clamp(blue, 0D, 1D) * 255D);

        this.r = (byte) (MathHelper.clamp(r, 0, 255));
        this.g = (byte) (MathHelper.clamp(g, 0, 255));
        this.b = (byte) (MathHelper.clamp(b, 0, 255));
    }

    /**
     * Creates a new RGB value.
     *
     * @param rgb The color as an integer.
     *
     * @since 2.0.0
     */
    public Rgb(int rgb) {
        this.r = (byte) ((rgb & RED_MASK) >> 16);
        this.g = (byte) ((rgb & GREEN_MASK) >> 8);
        this.b = (byte) (rgb & BLUE_MASK);
    }

    /**
     * Ensures that the color has a cached HSB conversion.
     *
     * @since 2.0.0
     */
    private void ensureHsb() {
        if (this.hsb == null || this.hsb.length != 3) {
            this.hsb = Color.RGBtoHSB(this.red(), this.green(), this.blue(), null);
        }
    }

    /**
     * Returns the color as an integer.
     *
     * @return The color.
     *
     * @since 2.0.0
     */
    public int integer() {
        final int r = this.red() << 16;
        final int g = this.green() << 8;
        final int b = this.blue();

        return r | g | b;
    }

    /**
     * Returns the color's red component.
     *
     * @return The red component.
     *
     * @since 2.0.0
     */
    public int red() {
        return this.r & 0xFF;
    }

    /**
     * Returns the color's green component.
     *
     * @return The green component.
     *
     * @since 2.0.0
     */
    public int green() {
        return this.g & 0xFF;
    }

    /**
     * Returns the color's blue component.
     *
     * @return The blue component.
     *
     * @since 2.0.0
     */
    public int blue() {
        return this.b & 0xFF;
    }

    /**
     * Returns the color's red component, scaled between the range {@code [0, 1]}.
     *
     * @return The red component.
     *
     * @since 2.0.0
     */
    public double redScaled() {
        return this.red() / 255D;
    }

    /**
     * Returns the color's green component, scaled between the range {@code [0, 1]}.
     *
     * @return The green component.
     *
     * @since 2.0.0
     */
    public double greenScaled() {
        return this.green() / 255D;
    }

    /**
     * Returns the color's blue component, scaled between the range {@code [0, 1]}.
     *
     * @return The blue component.
     *
     * @since 2.0.0
     */
    public double blueScaled() {
        return this.blue() / 255D;
    }

    /**
     * Returns the color's hue.
     * <p>
     * This value will be within a range of {@code [0, 1]}.
     *
     * @return The hue.
     *
     * @since 2.0.0
     */
    public float hue() {
        this.ensureHsb();

        return this.hsb[0];
    }

    /**
     * Returns the color's saturation.
     * <p>
     * This value will be within a range of {@code [0, 1]}.
     *
     * @return The saturation.
     *
     * @since 2.0.0
     */
    public float saturation() {
        this.ensureHsb();

        return this.hsb[1];
    }

    /**
     * Returns the color's brightness.
     * <p>
     * This value will be within a range of {@code [0, 1]}.
     *
     * @return The brightness.
     *
     * @since 2.0.0
     */
    public float brightness() {
        this.ensureHsb();

        return this.hsb[2];
    }

    /**
     * Sets the color's red component.
     * <p>
     * The given hue should be within a range of {@code [0, 255]}. If the value exceeds this, it will be clamped.
     *
     * @param red The new red component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withRed(int red) {
        return new Rgb(red, this.green(), this.blue());
    }

    /**
     * Sets the color's green component.
     * <p>
     * The given hue should be within a range of {@code [0, 255]}. If the value exceeds this, it will be clamped.
     *
     * @param green The new green component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withGreen(int green) {
        return new Rgb(this.red(), green, this.blue());
    }

    /**
     * Sets the color's blue component.
     * <p>
     * The given hue should be within a range of {@code [0, 255]}. If the value exceeds this, it will be clamped.
     *
     * @param blue The new blue component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withBlue(int blue) {
        return new Rgb(this.red(), this.green(), blue);
    }

    /**
     * Sets the color's red component.
     * <p>
     * The given hue should be within a range of {@code [0, 1]}. If the value exceeds this, it will be clamped.
     *
     * @param red The new red component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withRed(double red) {
        return new Rgb(red, this.greenScaled(), this.blueScaled());
    }

    /**
     * Sets the color's green component.
     * <p>
     * The given hue should be within a range of {@code [0, 1]}. If the value exceeds this, it will be clamped.
     *
     * @param green The new green component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withGreen(double green) {
        return new Rgb(this.redScaled(), green, this.blueScaled());
    }

    /**
     * Sets the color's blue component.
     * <p>
     * The given hue should be within a range of {@code [0, 1]}. If the value exceeds this, it will be clamped.
     *
     * @param blue The new blue component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withBlue(double blue) {
        return new Rgb(this.redScaled(), this.greenScaled(), blue);
    }

    /**
     * Sets the color's hue.
     * <p>
     * The given hue should be within a range of {@code [0, 1]}. If the value exceeds this, it will be wrapped.
     *
     * @param hue The new hue.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withHue(float hue) {
        this.ensureHsb();

        // Cycles the hue between 0 and 1.
        return new Rgb(hue % 1F, this.saturation(), this.brightness());
    }

    /**
     * Sets the color's saturation.
     * <p>
     * The given saturation should be within a range of {@code [0, 1]}. If the value exceeds this, it will be wrapped.
     *
     * @param saturation The new saturation.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withSaturation(float saturation) {
        this.ensureHsb();

        // Cycles the saturation between 0 and 1.
        return new Rgb(this.hue(), saturation % 1F, this.brightness());
    }

    /**
     * Sets the color's brightness.
     * <p>
     * The given brightness should be within a range of {@code [0, 1]}. If the value exceeds this, it will be wrapped.
     *
     * @param brightness The new brightness.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgb withBrightness(float brightness) {
        this.ensureHsb();

        // Cycles the brightness between 0 and 1.
        return new Rgb(this.hue(), this.saturation(), brightness % 1F);
    }

    /**
     * Maps each RGB color component using the given method.
     *
     * @param map The mapping method.
     *
     * @return A new, mapped color.
     *
     * @since 2.0.0
     */
    public Rgb map(IntUnaryOperator map) {
        final int r = map.applyAsInt(this.red());
        final int g = map.applyAsInt(this.green());
        final int b = map.applyAsInt(this.blue());

        return new Rgb(r, g, b);
    }

    /**
     * Mixes two colors together by combining their color components.
     *
     * @param other The other color.
     * @param mix The mixing method.
     *
     * @return A new, mixed color.
     *
     * @since 2.0.0
     */
    public Rgb mix(Rgb other, IntBinaryOperator mix) {
        final int r = mix.applyAsInt(this.red(), other.red());
        final int g = mix.applyAsInt(this.green(), other.green());
        final int b = mix.applyAsInt(this.blue(), other.blue());

        return new Rgb(r, g, b);
    }

}
