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

package dev.jaxydog.astral.content.effect;

import dev.jaxydog.astral.content.effect.CustomPotion.Recipe;
import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.register.ContentRegistrar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DyeColor;

@SuppressWarnings("unused")
public final class CustomPotions extends ContentRegistrar {

    public static final CustomPotion SINISTER = new CustomPotion(
        "sinister",
        new Recipe(Potions.THICK,
            Ingredient.ofItems(AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.RED).orElseThrow())
        ),
        new StatusEffectInstance(CustomStatusEffects.SINISTER, 15 * 20, 0)
    );

    public static final CustomPotion LONG_SINISTER = new CustomPotion(
        "long_sinister",
        new Recipe(CustomPotions.SINISTER, Ingredient.ofItems(Items.REDSTONE)),
        new StatusEffectInstance(CustomStatusEffects.SINISTER, 30 * 20, 0)
    );

    public static final CustomPotion STRONG_SINISTER = new CustomPotion(
        "strong_sinister",
        new Recipe(CustomPotions.SINISTER, Ingredient.ofItems(Items.GLOWSTONE_DUST)),
        new StatusEffectInstance(CustomStatusEffects.SINISTER, 10 * 20, 1)
    );

}
