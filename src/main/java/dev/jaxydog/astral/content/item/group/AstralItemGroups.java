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

package dev.jaxydog.astral.content.item.group;

import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.register.ContentRegistrar;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

/**
 * A container class that registers all {@link net.minecraft.item.ItemGroup} instances.
 *
 * @author Jaxydog
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public final class AstralItemGroups extends ContentRegistrar {

    /**
     * The mod's default item group.
     *
     * @since 2.0.0
     */
    public static final AstralItemGroup DEFAULT = AstralItemGroup.builder("default")
        .icon(Items.NETHER_STAR::getDefaultStack)
        .buildGroup();

    /**
     * The item group used to list all dyeable amethyst blocks.
     *
     * @since 2.0.0
     */
    public static final AstralCycledItemGroup DYEABLE_AMETHYST = AstralCycledItemGroup.builder("dyeable_amethyst")
        .icon(() -> AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.RED).orElseThrow().getDefaultStack())
        .icon(() -> AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.ORANGE).orElseThrow().getDefaultStack())
        .icon(() -> AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.YELLOW).orElseThrow().getDefaultStack())
        .icon(() -> AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.LIME).orElseThrow().getDefaultStack())
        .icon(() -> AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.LIGHT_BLUE).orElseThrow().getDefaultStack())
        .icon(() -> AstralItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.PURPLE).orElseThrow().getDefaultStack())
        .cycleInterval(60)
        .buildGroup();

    /**
     * The item group used to store all of Starmoney's lore items.
     *
     * @since 2.0.0
     */
    public static final AstralItemGroup STARMONEY_PLAZA = AstralItemGroup.builder("starmoney_plaza")
        .icon(Items.DRAGON_HEAD::getDefaultStack)
        .buildGroup();

}
