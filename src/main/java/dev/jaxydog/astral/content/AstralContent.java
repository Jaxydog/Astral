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

import dev.jaxydog.astral.content.block.CustomBlocks;
import dev.jaxydog.astral.content.data.CustomData;
import dev.jaxydog.astral.content.effect.CustomPotions;
import dev.jaxydog.astral.content.effect.CustomStatusEffects;
import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.content.item.group.AstralItemGroups;
import dev.jaxydog.astral.content.loader.AstralLoaders;
import dev.jaxydog.astral.content.power.CustomActions;
import dev.jaxydog.astral.content.power.CustomConditions;
import dev.jaxydog.astral.content.power.CustomPowers;
import dev.jaxydog.astral.content.sound.CustomSoundEvents;
import dev.jaxydog.astral.content.trinket.CustomTrinketPredicates;
import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.IgnoreRegistration;

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

    /** A container class that registers all gamerule {@link net.minecraft.world.GameRules.Key} instances. */
    public static final AstralGamerules GAMERULES = new AstralGamerules();
    /** A container class that registers all {@link net.minecraft.item.ItemGroup} instances. */
    public static final AstralItemGroups ITEM_GROUPS = new AstralItemGroups();
    /** A container class that registers all {@link net.minecraft.item.Item} instances. */
    public static final AstralItems ITEMS = new AstralItems();
    /** A container class that registers all data or asset loader instances. */
    public static final AstralLoaders LOADERS = new AstralLoaders();

    // Defined custom content classes
    public static final CustomActions ACTIONS = new CustomActions();
    public static final CustomBlocks BLOCKS = new CustomBlocks();
    public static final CustomConditions CONDITIONS = new CustomConditions();
    public static final CustomData DATA = new CustomData();
    public static final CustomPotions POTIONS = new CustomPotions();
    public static final CustomPowers POWERS = new CustomPowers();
    public static final CustomSoundEvents SOUND_EVENTS = new CustomSoundEvents();
    public static final CustomStatusEffects STATUS_EFFECTS = new CustomStatusEffects();
    public static final CustomTrinketPredicates TRINKET_SLOTS = new CustomTrinketPredicates();

}
