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

package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.registry.Registered.Client;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

/**
 * Implements common functionality for all colored item extensions.
 * <p>
 * By itself, this interface only handles registration on the client environment. It should usually be used in tandem
 * with the {@link Custom} interface, which handles common registration as well.
 *
 * @author Jaxydog
 */
public interface Colored extends Client, ItemConvertible {

    /**
     * Returns the color for the given stack at the provided layer index.
     *
     * @param stack The item stack.
     * @param layerIndex The index of the color layer.
     *
     * @return The layer's color.
     */
    int getStackColor(ItemStack stack, int layerIndex);

    @Override
    default void registerClient() {
        ColorProviderRegistry.ITEM.register(this::getStackColor, this.asItem());
    }

}
