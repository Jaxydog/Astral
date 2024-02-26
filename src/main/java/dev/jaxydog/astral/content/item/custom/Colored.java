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

package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.register.Registered;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

/**
 * Provides dynamic color support for the implementing item.
 *
 * @author Jaxydog
 */
public interface Colored extends ItemConvertible, Registered.Client {

    /**
     * Returns the color for the given stack at the provided layer index.
     *
     * @param stack The item stack
     * @param index The color layer index.
     *
     * @return The stack's color at the given index.
     */
    int getColor(ItemStack stack, int index);

    @Override
    default void registerClient() {
        ColorProviderRegistry.ITEM.register(this::getColor, this.asItem());
    }

}
