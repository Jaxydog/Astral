/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Icepenguin
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

import dev.jaxydog.astral.content.item.BottleItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Implements the chocolate milk item.
 * <p>
 * This item will apply effects to the consuming entity in the same manner as suspicious stew.
 *
 * @author Icepenguin
 * @author Jaxydog
 * @since 1.7.0
 */
public class ChocolateMilkItem extends BottleItem {

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     *
     * @since 2.0.0
     */
    public ChocolateMilkItem(
        String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup
    ) {
        super(path, settings, preferredGroup);
    }

    /**
     * Creates a new item using the given settings.
     * <p>
     * This item will be added to the default item group.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     *
     * @since 1.7.0
     */
    public ChocolateMilkItem(String path, Settings settings) {
        super(path, settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        // The functionality for this item is, for the most part, shared with suspicious stew.
        if (entity != null) SuspiciousStewItem.forEachEffect(stack, entity::addStatusEffect);

        // This *should* never fail. If it does, something's gone terribly wrong.
        assert entity != null;

        return super.finishUsing(stack, world, entity);
    }

}
