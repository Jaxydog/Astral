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

import dev.jaxydog.astral.content.item.custom.MirrorItem;
import dev.jaxydog.astral.content.item.group.AstralItemGroups;
import dev.jaxydog.astral.registry.ContentRegistrar;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Rarity;

/**
 * A container class that registers all {@link net.minecraft.item.Item} instances.
 *
 * @author Jaxydog
 */
@SuppressWarnings("unused")
public final class AstralItems extends ContentRegistrar {

    /** The Fluxling's mirror item. */
    public static final MirrorItem MIRROR = new MirrorItem(
        "mirror",
        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON),
        AstralItemGroups.DEFAULT::getRegistryKey
    );

}
