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

import org.jetbrains.annotations.ApiStatus.NonExtendable;

/**
 * Provides an interface for easily manipulating colors.
 *
 * @author Jaxydog
 * @since 2.0.0
 */
@NonExtendable
public interface ColorHelper {

    /**
     * Linearly interpolates between a starting and ending color.
     *
     * @param start The start color.
     * @param end The end color.
     * @param delta The amount to transition, with a range of {@code [0, 1]}.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    static Rgba transition(Rgba start, Rgba end, double delta) {
        // Scary math, just linearly interpolates between the start and end values per component.
        final double r = (end.redScaled() - start.redScaled()) * delta;
        final double g = (end.greenScaled() - start.greenScaled()) * delta;
        final double b = (end.blueScaled() - start.blueScaled()) * delta;
        final double a = (end.alphaScaled() - start.alphaScaled()) * delta;

        // Add the transitional colors into the base color.
        return new Rgba(start.redScaled() + r,
            start.greenScaled() + g,
            start.blueScaled() + b,
            start.alphaScaled() + a
        );
    }

    /**
     * Linearly interpolates between a starting and ending color.
     *
     * @param start The start color.
     * @param end The end color.
     * @param delta The amount to transition, with a range of {@code [0, 1]}.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    static Rgb transition(Rgb start, Rgb end, double delta) {
        // Scary math, just linearly interpolates between the start and end values per component.
        final double r = (end.redScaled() - start.redScaled()) * delta;
        final double g = (end.greenScaled() - start.greenScaled()) * delta;
        final double b = (end.blueScaled() - start.blueScaled()) * delta;

        // Add the transitional colors into the base color.
        return new Rgb(start.redScaled() + r, start.greenScaled() + g, start.blueScaled() + b);
    }

    /**
     * Linearly interpolates between a starting and ending color.
     *
     * @param start The start color.
     * @param end The end color.
     * @param delta The amount to transition, with a range of {@code [0, 1]}.
     *
     * @return A new color.
     *
     * @since 2.0.0
     */
    static int transition(int start, int end, double delta) {
        return transition(new Rgba(start), new Rgba(end), delta).integer();
    }

}
