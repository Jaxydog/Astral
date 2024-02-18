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

import dev.jaxydog.astral.register.ContentRegistrar;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

/**
 * Contains definitions for all custom item groups.
 *
 * @author Jaxydog
 */
@SuppressWarnings("unused")
public final class CustomItemGroups extends ContentRegistrar {

    public static final CustomItemGroup DEFAULT = CustomItemGroup.builder("default")
        .icon(Items.NETHER_STAR::getDefaultStack)
        .finish();

    public static final CustomCycledItemGroup DYEABLE_AMETHYST = CustomCycledItemGroup.builder("dyeable_amethyst")
        .icon(() -> CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.RED).orElseThrow().getDefaultStack())
        .icon(() -> CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.ORANGE).orElseThrow().getDefaultStack())
        .icon(() -> CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.YELLOW).orElseThrow().getDefaultStack())
        .icon(() -> CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.LIME).orElseThrow().getDefaultStack())
        .icon(() -> CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.LIGHT_BLUE).orElseThrow().getDefaultStack())
        .icon(() -> CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.PURPLE).orElseThrow().getDefaultStack())
        .interval(60)
        .finish();

    public static final CustomItemGroup STARMONEY_PLAZA = CustomItemGroup.builder("starmoney_plaza")
        .icon(Items.DRAGON_HEAD::getDefaultStack)
        .finish();

}
