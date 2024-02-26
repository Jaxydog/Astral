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

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * An armor item that implements the {@link Cloudy} interface and is dyeable.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 */
@SuppressWarnings("unused")
public class DyeableCloudyArmorItem extends CloudyArmorItem implements DyeableItem {

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
    public DyeableCloudyArmorItem(
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
    public DyeableCloudyArmorItem(String path, ArmorMaterial material, Type type, Settings settings) {
        super(path, material, type, settings);
    }

    @Override
    public int getStackColor(ItemStack stack, int index) {
        return switch (index) {
            // The base layer should be leather.
            case 0 -> this.getColor(stack);
            // The overlay layer should be cloudy fluff.
            case 1 -> this.getStorminessColor(stack);
            // Everything else is untouched.
            default -> 0xFF_FF_FF;
        };
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // This super-call handles updating storminess values for us.
        super.inventoryTick(stack, world, entity, slot, selected);

        // Get all equipped armor items that are instances of this class.
        final List<ItemStack> armor = Lists.newArrayList(entity.getArmorItems());
        final boolean matching = armor.stream().allMatch(s -> s.getItem() instanceof DyeableCloudyArmorItem);

        // Apply the effects only when wearing a full set.
        if (armor.size() == 4 && matching && entity instanceof final LivingEntity living) {
            // Compute an average value from the combined storminess values.
            final double level = armor.stream().map(this::getStorminess).reduce(0D, Double::sum) / 4D;
            // If the average is below 0.5D or 50%, apply slowness instead of jump boost.
            final StatusEffect type = level < 0.5D ? StatusEffects.JUMP_BOOST : StatusEffects.SLOWNESS;

            // Effects should last at least 20 ticks or 1 second to prevent any weirdness.
            living.addStatusEffect(new StatusEffectInstance(type, 20, 0, false, false));
        }
    }

    @Override
    public ItemStack getDefaultStack() {
        final ItemStack stack = super.getDefaultStack();

        this.setColor(stack, DyeableItem.DEFAULT_COLOR);

        return stack;
    }

}
