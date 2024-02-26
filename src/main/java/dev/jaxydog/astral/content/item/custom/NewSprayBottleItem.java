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

import dev.jaxydog.astral.content.item.AstralItem;
import dev.jaxydog.astral.content.sound.SoundContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A simple spray bottle item.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 */
@SuppressWarnings("unused")
public class NewSprayBottleItem extends AstralItem implements Sprayed {

    /** The sound played when extinguishing fire. */
    public static final SoundContext EXTINGUISH_SOUND = new SoundContext(SoundEvents.BLOCK_FIRE_EXTINGUISH,
        SoundCategory.BLOCKS,
        0.5F,
        2.6F,
        0.8F
    );

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     */
    public NewSprayBottleItem(
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
     */
    public NewSprayBottleItem(String path, Settings settings) {
        super(path, settings);
    }

    @Override
    public void invokeSpray(ItemStack stack, World world, @Nullable LivingEntity entity, Entity target, int charges) {
        if (this.canSpray(stack, entity, target, charges)) {

        }

        Sprayed.super.invokeSpray(stack, world, entity, target, charges);
    }

}
