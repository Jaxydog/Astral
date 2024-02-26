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

package dev.jaxydog.astral.content.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

/**
 * Provides common functionality for items that are expected to contain the {@value #CUSTOM_MODEL_DATA_KEY} NBT key.
 *
 * @author Jaxydog
 */
public interface Customized {

    /**
     * The NBT key corresponding to an item's custom model identifier.
     * <p>
     * This is directly taken from
     * {@link net.minecraft.client.item.ModelPredicateProviderRegistry#CUSTOM_MODEL_DATA_KEY}, where it is defined as a
     * private static constant.
     */
    @SuppressWarnings("JavadocReference")
    String CUSTOM_MODEL_DATA_KEY = "CustomModelData";

    /**
     * Returns the given stack's assigned custom model data.
     * <p>
     * If the key is present but is not assigned to a valid number, {@code Optional.of(0)} is returned.
     *
     * @param stack The item stack.
     *
     * @return The custom model data.
     */
    default Optional<Integer> getCustomModelData(ItemStack stack) {
        final NbtCompound compound = stack.getNbt();

        if (compound == null || !compound.contains(CUSTOM_MODEL_DATA_KEY)) {
            return Optional.empty();
        } else {
            return Optional.of(compound.getInt(CUSTOM_MODEL_DATA_KEY));
        }
    }

    /**
     * Sets the given stack's custom model data.
     *
     * @param stack The item stack.
     * @param data The custom model data.
     */
    default void setCustomModelData(ItemStack stack, int data) {
        stack.getOrCreateNbt().putInt(CUSTOM_MODEL_DATA_KEY, data);
    }

}
