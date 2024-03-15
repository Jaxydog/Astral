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
import dev.jaxydog.astral.datagen.ModelGenerator;
import dev.jaxydog.astral.register.Registered.Generated;
import net.minecraft.data.client.Models;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A custom item that randomly applies a specified effect.
 *
 * @author Jaxydog
 * @since 1.7.0
 */
public class RandomEffectItem extends AstralItem implements Generated {

    /**
     * The chance that the effect will be applied.
     *
     * @since 1.7.0
     */
    private final float effectChance;
    /**
     * The status effect to be given to the living entity.
     *
     * @since 1.7.0
     */
    private final StatusEffect effect;

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     * @param effectChance The chance that the effect will be applied.
     * @param effect The status effect to be given to the living entity.
     *
     * @since 2.0.0
     */
    public RandomEffectItem(
        String path,
        Settings settings,
        @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup,
        float effectChance,
        StatusEffect effect
    ) {
        super(path, settings, preferredGroup);

        this.effectChance = effectChance;
        this.effect = effect;
    }

    /**
     * Creates a new item using the given settings.
     * <p>
     * This item will be added to the default item group.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param effectChance The chance that the effect will be applied.
     * @param effect The status effect to be given to the living entity.
     *
     * @since 1.7.0
     */
    public RandomEffectItem(String path, Settings settings, float effectChance, StatusEffect effect) {
        super(path, settings);

        this.effectChance = effectChance;
        this.effect = effect;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // This is called first, in case any computation needs to be handled before applying the effect.
        super.inventoryTick(stack, world, entity, slot, selected);

        // Only run on the server for living entities.
        if (world.isClient() || !(entity instanceof final LivingEntity livingEntity)) return;
        // Don't re-apply when the entity has the target effect.
        if (livingEntity.hasStatusEffect(this.effect)) return;

        if (world.getRandom().nextFloat() <= this.effectChance) {
            // We need to make a new instance every time, otherwise the duration will be shared between invocations and
            // cause weird behavior.
            final StatusEffectInstance instance = new StatusEffectInstance(this.effect, 200, 0, false, false, false);

            // We also need to ensure that the entity can have the effect in the first place.
            if (livingEntity.canHaveStatusEffect(instance)) livingEntity.addStatusEffect(instance);
        }
    }

    @Override
    public void generate() {
        // Generates a simple held item model.
        ModelGenerator.getInstance().generateItem(g -> g.register(this, Models.GENERATED));
    }

}
