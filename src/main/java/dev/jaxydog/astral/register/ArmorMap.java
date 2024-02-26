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

import net.minecraft.item.ArmorItem.Type;

import java.util.Set;
import java.util.function.BiFunction;

public class ArmorMap<T extends Registered> extends RegisteredMap<Type, T> {

    public ArmorMap(String rawId, BiFunction<String, Type, T> constructor) {
        super(rawId, constructor);
    }

    @Override
    protected final int compareKeys(Type a, Type b) {
        return a.compareTo(b);
    }

    @Override
    public final Set<Type> keys() {
        return Set.of(Type.values());
    }

    @Override
    public final String getIdPath(Type key) {
        return String.format("%s_%s", this.getRegistryPath(), key.getName());
    }

}
