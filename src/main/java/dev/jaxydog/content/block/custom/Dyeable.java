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

package dev.jaxydog.content.block.custom;

import dev.jaxydog.content.item.CustomBlockItem;
import dev.jaxydog.datagen.TextureGenerator;
import dev.jaxydog.register.Registered;
import dev.jaxydog.utility.ColorUtil.Rgb;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Optional;

/**
 * Common methods and implementations for dyeable blocks.
 *
 * @author Jaxydog
 */
public interface Dyeable extends Registered {

    /**
     * Computes the given color rotated and adjusted to match the provided dye color.
     *
     * @param color The base color.
     * @param dyeColor The dye color.
     *
     * @return The adjusted color.
     */
    static int computeColor(int color, DyeColor dyeColor) {
        final int alpha = color & 0xFF_00_00_00;

        if (alpha == 0) return color;

        final Rgb rgb = new Rgb(color);
        final float[] hsb = rgb.hsb();

        final Rgb adjusted = switch (dyeColor) {
            // Some colors have special procedures entirely.
            case WHITE -> rgb.withSaturation(0F);
            case LIGHT_GRAY -> rgb.withSaturation(0F).withBrightness(hsb[2] - 0.225F);
            case GRAY -> rgb.withSaturation(0F).withBrightness(hsb[2] - 0.45F);
            case BLACK -> rgb.withSaturation(0F).withBrightness(hsb[2] - 0.6F);
            // Everything else just hue rotates.
            default -> {
                final float[] components = dyeColor.getColorComponents();
                final Rgb target = new Rgb(components[0], components[1], components[2]);
                final Rgb rotated = target.withHue(target.hue());

                yield switch (dyeColor) {
                    // Some colors require a bit of fine-tuning.
                    case BROWN -> rotated.withBrightness(hsb[2] - 0.225F);
                    case GREEN -> rotated.withSaturation(hsb[1] + 0.375F).withBrightness(hsb[2] - 0.1F);
                    case PINK -> rotated.withSaturation(hsb[1] - 0.1F).withBrightness(hsb[2] + 0.1F);
                    case CYAN -> rotated.withSaturation(hsb[1] + 0.25F).withBrightness(hsb[2] - 0.25F);
                    // Everything else just gets a bit of saturation.
                    default -> rotated.withSaturation(hsb[1] + 0.25F);
                };
            }
        };

        // Clear the alpha in case there's some artifacts, then re-set it to be the initial alpha.
        return (adjusted.asInt() & 0x00_FF_FF_FF) | alpha;
    }

    /**
     * Computes the given colors of an entire image, adjusted to match the provided dye color.
     *
     * @param image The image to mutate.
     * @param dyeColor The dye color.
     */
    static void computeColor(BufferedImage image, DyeColor dyeColor) {
        final RescaleOp contrastPass = new RescaleOp(switch (dyeColor) {
            case GRAY -> 0.625F;
            case BLACK -> 0.5F;
            case GREEN -> 0.875F;
            default -> 0F;
        }, 0xF, null);

        contrastPass.filter(image, image);
    }

    /**
     * Generates a texture for the given dye color based off of the image referenced by the given source path.
     *
     * @param instance The generator instance.
     * @param sourcePath The expected source path.
     * @param dyeColor The dye color.
     * @param <T> The generation instance type.
     */
    static <T> void generateTexture(
        TextureGenerator.Instance<T> instance, String sourcePath, DyeColor dyeColor, Identifier identifier
    ) {
        final Optional<BufferedImage> maybeSource = instance.getImage(sourcePath);

        if (maybeSource.isEmpty()) return;

        final BufferedImage source = maybeSource.get();
        final BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());

        for (int y = 0; y < source.getHeight(); y += 1) {
            for (int x = 0; x < source.getHeight(); x += 1) {
                final int color = source.getRGB(x, y);
                final int computed = computeColor(color, dyeColor);

                image.setRGB(x, y, computed);
            }
        }

        computeColor(image, dyeColor);

        instance.generate(identifier, image);
    }

    /**
     * Returns the dye color corresponding to this block.
     *
     * @return The dye color.
     */
    DyeColor getDyeColor();

    /**
     * Returns the block item corresponding to this block.
     *
     * @return The block item.
     */
    CustomBlockItem getBlockItem();

}
