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

package dev.jaxydog.astral.utility.injected;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Extends a {@link net.minecraft.client.world.ClientWorld}.
 *
 * @author Jaxydog
 */
@Environment(EnvType.CLIENT)
public interface AstralClientWorld {

    /**
     * Plays a sound at the given position without limiting its volume or pitch.
     *
     * @param x The X position.
     * @param y The Y position.
     * @param z The Z position.
     * @param event The sound event.
     * @param category The sound category.
     * @param volume The sound volume.
     * @param pitch The sound pitch.
     * @param useDistance Whether to check distance in the sound engine.
     * @param seed The seed for the sound's randomness.
     */
    void astral$playUnboundedSound(
        double x,
        double y,
        double z,
        SoundEvent event,
        SoundCategory category,
        float volume,
        float pitch,
        boolean useDistance,
        long seed
    );

    /**
     * Plays a sound from the given entity without limiting its volume or pitch.
     *
     * @param except Possibly a player.
     * @param entity The target entity.
     * @param event The sound event.
     * @param category The sound category.
     * @param volume The sound volume.
     * @param pitch The sound pitch.
     * @param seed The seed for the sound's randomness.
     */
    void astral$playUnboundedSoundFromEntity(
        @Nullable PlayerEntity except,
        Entity entity,
        SoundEvent event,
        SoundCategory category,
        float volume,
        float pitch,
        long seed
    );

}
