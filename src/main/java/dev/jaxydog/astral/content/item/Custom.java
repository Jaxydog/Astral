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

import dev.jaxydog.astral.content.item.group.CustomItemGroups;
import dev.jaxydog.astral.register.Registered.Common;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.Optional;

/**
 * Implements common functionality for all item extensions.
 * <p>
 * When creating a new item extension, this interface should be implemented.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 */
public interface Custom extends Common, ItemConvertible {

    /**
     * Returns this item's preferred item group.
     *
     * @return A reference to an item group.
     */
    default Optional<RegistryKey<ItemGroup>> getItemGroup() {
        return Optional.of(CustomItemGroups.DEFAULT.getRegistryKey());
    }

    @Override
    default void register() {
        Registry.register(Registries.ITEM, this.getRegistryId(), this.asItem());

        // If the preferred group is non-empty, add this item.
        this.getItemGroup().ifPresent(group -> ItemGroupEvents.modifyEntriesEvent(group).register(g -> g.add(this)));
    }

}
