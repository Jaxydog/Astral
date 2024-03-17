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

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * Stores and allows for the manipulation of an RGBA value.
 *
 * @author Jaxydog
 * @since 2.0.0
 */
public class Rgba extends Rgb {

    /**
     * The color's alpha component.
     *
     * @since 2.0.0
     */
    private final byte a;

    /**
     * Creates a new RGBA value.
     * <p>
     * The provided values are expected to be within a range of {@code [0, 255]}. If a value exceeds this, it will be
     * clamped.
     *
     * @param red The red component.
     * @param green The green component.
     * @param blue The blue component.
     * @param alpha The alpha component.
     *
     * @since 2.0.0
     */
    public Rgba(int red, int green, int blue, int alpha) {
        super(red, green, blue);

        this.a = (byte) (MathHelper.clamp(alpha, 0, 255));
    }

    /**
     * Creates a new RGBA value.
     * <p>
     * The provided values are expected to be within a range of {@code [0, 1]}. If a value exceed this, it will be
     * clamped.
     *
     * @param red The red component.
     * @param green The green component.
     * @param blue The blue component.
     * @param alpha The alpha component.
     *
     * @since 2.0.0
     */
    public Rgba(double red, double green, double blue, double alpha) {
        super(red, green, blue);

        // Clamp between 0 and 1, then scale up to [0, 255].
        final int a = (int) (MathHelper.clamp(alpha, 0D, 1D) * 255D);

        this.a = (byte) (MathHelper.clamp(a, 0, 255));
    }

    /**
     * Creates a new RGBA value.
     *
     * @param rgba The color as an integer.
     *
     * @since 2.0.0
     */
    public Rgba(int rgba) {
        super(rgba);

        this.a = (byte) ((rgba & ALPHA_MASK) >> 24);
    }

    /**
     * Creates a new RGBA value.
     *
     * @param rgb The RBG value.
     * @param alpha The alpha component.
     *
     * @since 2.0.0
     */
    public Rgba(Rgb rgb, int alpha) {
        this(rgb.red(), rgb.green(), rgb.blue(), (byte) (MathHelper.clamp(alpha, 0, 255)));
    }

    /**
     * Creates a new RGBA value.
     *
     * @param rgb The RBG value.
     * @param alpha The alpha component.
     *
     * @since 2.0.0
     */
    public Rgba(Rgb rgb, double alpha) {
        this(rgb, (int) (MathHelper.clamp(alpha, 0D, 1D) * 255D));
    }

    /**
     * Returns the color's alpha component.
     *
     * @return The alpha component.
     *
     * @since 2.0.0
     */
    public int alpha() {
        return this.a & 0xFF;
    }

    /**
     * Returns the color's alpha component, scaled between the range {@code [0, 1]}.
     *
     * @return The alpha component.
     *
     * @since 2.0.0
     */
    public double alphaScaled() {
        return this.alpha() / 255D;
    }

    /**
     * Sets the color's alpha component.
     * <p>
     * The given hue should be within a range of {@code [0, 255]}. If the value exceeds this, it will be clamped.
     *
     * @param alpha The new alpha component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgba withAlpha(int alpha) {
        return new Rgba(this.red(), this.green(), this.blue(), alpha);
    }

    /**
     * Sets the color's alpha component.
     * <p>
     * The given hue should be within a range of {@code [0, 1]}. If the value exceeds this, it will be clamped.
     *
     * @param alpha The new alpha component.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    public Rgba withAlpha(double alpha) {
        return new Rgba(this.redScaled(), this.greenScaled(), this.blueScaled(), alpha);
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
    public Rgba mix(Rgba other, IntBinaryOperator mix) {
        final int a = mix.applyAsInt(this.alpha(), other.alpha());

        return new Rgba(super.mix(other, mix), a);
    }

    @Override
    public int integer() {
        return super.integer() | (this.alpha() << 24);
    }

    @Override
    public Rgba withRed(int red) {
        return new Rgba(super.withRed(red), this.alpha());
    }

    @Override
    public Rgba withRed(double red) {
        return new Rgba(super.withRed(red), this.alpha());
    }

    @Override
    public Rgba withGreen(int green) {
        return new Rgba(super.withGreen(green), this.alpha());
    }

    @Override
    public Rgba withGreen(double green) {
        return new Rgba(super.withGreen(green), this.alpha());
    }

    @Override
    public Rgba withBlue(int blue) {
        return new Rgba(super.withBlue(blue), this.alpha());
    }

    @Override
    public Rgba withBlue(double blue) {
        return new Rgba(super.withBlue(blue), this.alpha());
    }

    @Override
    public Rgba withHue(float hue) {
        return new Rgba(super.withHue(hue), this.alpha());
    }

    @Override
    public Rgb withSaturation(float saturation) {
        return new Rgba(super.withSaturation(saturation), this.alpha());
    }

    @Override
    public Rgb withBrightness(float brightness) {
        return new Rgba(super.withBrightness(brightness), this.alpha());
    }

    @Override
    public Rgba map(IntUnaryOperator map) {
        final int a = map.applyAsInt(this.alpha());

        return new Rgba(super.map(map), a);
    }

}
