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

package dev.jaxydog.utility;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

/**
 * Plays a sound from an entity that has its pitch and volume effectively unbounded.
 * <p>
 * Only positive values are allowed, and any provided negative number will be clamped to {@code 0F}.
 *
 * @author Jaxydog
 */
public class PlayUnboundedSoundFromEntityS2CPacket extends PlaySoundFromEntityS2CPacket {

    @SuppressWarnings("unused")
    public PlayUnboundedSoundFromEntityS2CPacket(
        RegistryEntry<SoundEvent> sound, SoundCategory category, Entity entity, float volume, float pitch, long seed
    ) {
        super(sound, category, entity, volume, pitch, seed);
    }

    public PlayUnboundedSoundFromEntityS2CPacket(PacketByteBuf buffer) {
        super(buffer);
    }

}
