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

package dev.jaxydog.content.item;

import dev.jaxydog.register.ContentRegistrar;
import net.minecraft.item.Items;

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

    public static final CustomItemGroup DYEABLE_AMETHYST = CustomItemGroup.builder("dyeable_amethyst")
        .icon(Items.AMETHYST_CLUSTER::getDefaultStack)
        .finish();

    public static final CustomItemGroup STARMONEY_PLAZA = CustomItemGroup.builder("starmoney_plaza")
        .icon(Items.DRAGON_HEAD::getDefaultStack)
        .finish();

}
