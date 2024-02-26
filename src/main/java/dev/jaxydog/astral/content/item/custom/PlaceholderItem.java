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

import dev.jaxydog.astral.content.item.AstralItem;
import dev.jaxydog.astral.content.item.Customizable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Implements a placeholder item, or an item that exists solely to be replaced with other models / textures.
 * <p>
 * The expected model, texture(s), and name of this item are controlled via the item stack's custom model data.
 *
 * @author Jaxydog
 */
public class PlaceholderItem extends AstralItem implements Customizable, Equipment {

    /** The item's cached translation keys. */
    private static final Map<Integer, String> TRANSLATION_KEYS = new Object2ObjectOpenHashMap<>();

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     */
    public PlaceholderItem(String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup) {
        super(path, settings, preferredGroup);
    }

    /**
     * Creates a new item using the given settings.
     * <p>
     * This item will be added to the default item group.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     */
    public PlaceholderItem(String path, Settings settings) {
        super(path, settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        final int data = this.getCustomModelData(stack).orElse(0);

        // Assume that a value of 0 means that the data is unset, and return the default translation key.
        if (data == 0) return super.getTranslationKey(stack);

        // Cache translation keys to reduce repeated computation.
        if (!TRANSLATION_KEYS.containsKey(data)) {
            // Computed to a value of roughly `item.astral.placeholder.1`.
            TRANSLATION_KEYS.put(data, super.getTranslationKey(stack) + "." + data);
        }

        return TRANSLATION_KEYS.get(data);
    }

    // This override allows the player to wear this item on their head.
    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

}
