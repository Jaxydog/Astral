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

import net.minecraft.item.ArmorItem.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * A map containing values assigned to armor types.
 *
 * @param <V> The type of the value stored within this map.
 *
 * @author Jaxydog
 * @since 1.3.0
 */
public class ArmorMap<V extends Registered> extends RegisteredMap<Type, V> {

    /**
     * Creates a new registered map.
     *
     * @param basePath The base identifier path.
     * @param computeCallback The value computation callback.
     *
     * @since 1.3.0
     */
    public ArmorMap(@NotNull String basePath, BiFunction<@NotNull String, @NotNull Type, V> computeCallback) {
        super(basePath, computeCallback);
    }

    @Override
    public Set<@NotNull Type> keys() {
        return Set.of(Type.values());
    }

    @Override
    protected int compareKeys(@NotNull Type left, @NotNull Type right) {
        return left.compareTo(right);
    }

    @Override
    protected String getPath(@NotNull Type key) {
        return "%s_%s".formatted(this.getRegistryPath(), key.getName());
    }

}
