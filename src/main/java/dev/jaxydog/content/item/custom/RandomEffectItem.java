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

package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.content.item.CustomItemGroup;
import dev.jaxydog.datagen.ModelGenerator;
import dev.jaxydog.register.Generated;
import net.minecraft.data.client.Models;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * A custom item that randomly applies the specified effect.
 *
 * @author Jaxydog
 */
public class RandomEffectItem extends CustomItem implements Generated {

    private final float effectChance;
    private final StatusEffect effect;

    public RandomEffectItem(
        String idPath, Settings settings, @Nullable CustomItemGroup group, float effectChance, StatusEffect effect
    ) {
        super(idPath, settings, group);

        this.effectChance = effectChance;
        this.effect = effect;
    }

    @SuppressWarnings("unused")
    public RandomEffectItem(
        String idPath, Settings settings, float effectChance, StatusEffect effect
    ) {
        super(idPath, settings);

        this.effectChance = effectChance;
        this.effect = effect;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient() || !(entity instanceof final LivingEntity livingEntity)) return;
        if (livingEntity.hasStatusEffect(this.effect)) return;

        if (world.getRandom().nextFloat() <= this.effectChance) {
            final StatusEffectInstance instance = new StatusEffectInstance(this.effect, 200, 0, false, false, false);

            if (livingEntity.canHaveStatusEffect(instance)) livingEntity.addStatusEffect(instance);
        }
    }

    @Override
    public void generate() {
        ModelGenerator.getInstance().generateItem(g -> g.register(this, Models.GENERATED));
    }

}
