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

import dev.jaxydog.astral.content.item.AstralArmorItem;
import dev.jaxydog.astral.content.item.Colored;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * An armor item that implements the {@link Cloudy} interface.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 */
public class CloudyArmorItem extends AstralArmorItem implements Cloudy, Colored {

    /**
     * Creates a new armor item using the given settings.
     * <p>
     * If the {@code #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param material The armor's material.
     * @param type The armor's type.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     */
    public CloudyArmorItem(
        String path,
        ArmorMaterial material,
        Type type,
        Settings settings,
        @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup
    ) {
        super(path, material, type, settings, preferredGroup);
    }

    /**
     * Creates a new armor item using the given settings.
     * <p>
     * This item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param material The armor's material.
     * @param type The armor's type.
     * @param settings The item's settings.
     */
    public CloudyArmorItem(String path, ArmorMaterial material, Type type, Settings settings) {
        super(path, material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(this.getStorminessText(stack));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public int getArmorTextureLayers(ItemStack stack) {
        return 3;
    }

    @Override
    public double getDecreaseDelta(ItemStack stack) {
        return 1D / 320D;
    }

    @Override
    public double getIncreaseDelta(ItemStack stack) {
        return 1D / 160D;
    }

    @Override
    public int getStackColor(ItemStack stack, int index) {
        return index == 0 ? this.getStorminessColor(stack) : 0xFF_FF_FF;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        this.updateStorminess(stack, entity);

        super.inventoryTick(stack, world, entity, slot, selected);
    }

}
