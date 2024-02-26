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

package dev.jaxydog.astral.register;

import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * A map containing values assigned to dye colors.
 *
 * @param <V> The type of the value stored within this map.
 *
 * @author Jaxydog
 */
public class DyeableMap<V extends Registered> extends RegisteredMap<DyeColor, V> {

    /**
     * A list containing all dye colors in the standard Minecraft ordering.
     * <p>
     * This is used to control the registration order of values stored within this map.
     */
    private static final List<DyeColor> ORDERING = List.of(
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

    /**
     * Creates a new registered map.
     *
     * @param basePath The base identifier path.
     * @param computeCallback The value computation callback.
     */
    public DyeableMap(@NotNull String basePath, BiFunction<@NotNull String, @NotNull DyeColor, V> computeCallback) {
        super(basePath, computeCallback);
    }

    @Override
    public Set<@NotNull DyeColor> keys() {
        return Set.of(DyeColor.values());
    }

    @Override
    protected int compareKeys(@NotNull DyeColor left, @NotNull DyeColor right) {
        // Sorts in rainbow order to be aligned with Minecraft's standard color ordering.
        return Integer.compare(ORDERING.indexOf(left), ORDERING.indexOf(right));
    }

    @Override
    protected String getPath(@NotNull DyeColor key) {
        return "%s_%s".formatted(key.asString(), this.getRegistryPath());
    }

}
