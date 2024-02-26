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

package dev.jaxydog.astral.register;

import net.minecraft.util.DyeColor;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class DyeableMap<T extends Registered> extends RegisteredMap<DyeColor, T> {

    private static final List<DyeColor> ORDER = List.of(
        DyeColor.WHITE,
        DyeColor.LIGHT_GRAY,
        DyeColor.GRAY,
        DyeColor.BLACK,
        DyeColor.BROWN,
        DyeColor.RED,
        DyeColor.ORANGE,
        DyeColor.YELLOW,
        DyeColor.LIME,
        DyeColor.GREEN,
        DyeColor.CYAN,
        DyeColor.LIGHT_BLUE,
        DyeColor.BLUE,
        DyeColor.PURPLE,
        DyeColor.MAGENTA,
        DyeColor.PINK
    );

    public DyeableMap(String rawId, BiFunction<String, DyeColor, T> constructor) {
        super(rawId, constructor);
    }

    @Override
    public final Set<DyeColor> keys() {
        return Set.of(DyeColor.values());
    }

    @Override
    public final String getIdPath(DyeColor key) {
        return String.format("%s_%s", key.asString(), this.getRegistryPath());
    }

    @Override
    protected final int compareKeys(DyeColor a, DyeColor b) {
        // Sorts in rainbow order to be aligned with Minecraft's standard color ordering.
        return Integer.compare(ORDER.indexOf(a), ORDER.indexOf(b));
    }

}
