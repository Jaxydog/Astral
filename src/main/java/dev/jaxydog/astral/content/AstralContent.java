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

package dev.jaxydog.astral.content;

import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.content.item.group.AstralItemGroups;
import dev.jaxydog.astral.registry.ContentRegistrar;
import dev.jaxydog.astral.registry.IgnoreRegistration;

/**
 * A container class housing singletons that register all mod content.
 * <p>
 * The sole purpose of this class (aside from keeping all mod data in one place) is to support the automatic
 * registration system.
 * <p>
 * The instance of this class ({@link #INSTANCE}) should be registered within each mod entrypoint, or an entire
 * environment could be cut off from the registration system.
 *
 * @author Jaxydog
 */
@SuppressWarnings("unused")
public final class AstralContent extends ContentRegistrar {

    /**
     * The primary instance of {@link AstralContent}.
     * <p>
     * This field is ignored during the automatic registration process, and as such should be registered directly within
     * each mod entrypoint.
     * <p>
     * Failure to register within an entrypoint cuts off <i>an entire environment</i> from being registered.
     */
    @IgnoreRegistration
    public static final AstralContent INSTANCE = new AstralContent();

    /** A {@link ContentRegistrar} instance containing all {@link net.minecraft.item.ItemGroup}s. */
    public static final AstralItemGroups ITEM_GROUPS = new AstralItemGroups();

    /** A {@link ContentRegistrar} instance containing all {@link net.minecraft.item.Item}s. */
    public static final AstralItems ITEMS = new AstralItems();

}
