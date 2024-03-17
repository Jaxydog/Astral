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

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Converts an image into a new, adjusted form.
 *
 * @param color The color converter.
 * @param image The image consumer, for applying image-wide filters.
 *
 * @author Jaxydog
 * @since 2.0.0
 */
public record ImageMapper(ColorMapper color, Consumer<BufferedImage> image) {

    /**
     * Processes and converts the provided image into a new, adjusted image.
     *
     * @param image The base image.
     *
     * @return A new image.
     *
     * @since 2.0.0
     */
    public BufferedImage convert(BufferedImage image) {
        final BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int y = 0; y < image.getHeight(); y += 1) {
            for (int x = 0; x < image.getWidth(); x += 1) {
                final Rgba color = new Rgba(image.getRGB(x, y));

                // Ignore the pixel if it's transparent to save computation.
                if (color.alpha() == 0) continue;

                converted.setRGB(x, y, this.color.map(color).integer());
            }
        }

        this.image().accept(converted);

        return converted;
    }

}
