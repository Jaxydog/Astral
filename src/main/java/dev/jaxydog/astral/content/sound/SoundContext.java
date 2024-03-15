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

package dev.jaxydog.astral.content.sound;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * A sound context.
 * <p>
 * This is most useful for playing sounds that generally share the same traits without repeating code.
 *
 * @param event The sound event.
 * @param category The sound category.
 * @param volume The sound volume.
 * @param pitch The sound pitch.
 * @param pitchVariance The amount of variation in the sound pitch.
 *
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public record SoundContext(SoundEvent event, SoundCategory category, float volume, float pitch, float pitchVariance) {

    /** A player's burping sound. */
    public static final SoundContext BURP = new SoundContext(SoundEvents.ENTITY_PLAYER_BURP,
        SoundCategory.PLAYERS,
        0.5F,
        0.9F,
        0.1F
    );

    /**
     * A sound context.
     * <p>
     * This is most useful for playing sounds that generally share the same traits without repeating code.
     * <p>
     * By default, a pitch variance of {@code 0.625F} is used.
     *
     * @param event The sound event.
     * @param category The sound category.
     * @param volume The sound volume.
     * @param pitch The sound pitch.
     *
     * @since 2.0.0
     */
    public SoundContext(SoundEvent event, SoundCategory category, float volume, float pitch) {
        // A variance of `0.0625F` should be fine.
        this(event, category, volume, pitch, 0.0625F);
    }

    /**
     * A sound context.
     * <p>
     * This is most useful for playing sounds that generally share the same traits without repeating code.
     * <p>
     * By default, a volume of {@code 1F} and a pitch variance of {@code 0.625F} is used.
     *
     * @param event The sound event.
     * @param category The sound category.
     * @param pitch The sound pitch.
     *
     * @since 2.0.0
     */
    public SoundContext(SoundEvent event, SoundCategory category, float pitch) {
        // 1F is full volume.
        this(event, category, 1F, pitch);
    }

    /**
     * A sound context.
     * <p>
     * This is most useful for playing sounds that generally share the same traits without repeating code.
     * <p>
     * By default, a volume of {@code 1F}, a pitch of {@code 1F}, and a pitch variance of {@code 0.625F} is used.
     *
     * @param event The sound event.
     * @param category The sound category.
     *
     * @since 2.0.0
     */
    public SoundContext(SoundEvent event, SoundCategory category) {
        // 1F is normal pitch.
        this(event, category, 1F);
    }

    /**
     * Returns the sound pitch with a random variance applied.
     *
     * @param random A random number generator.
     *
     * @return A varied pitch.
     *
     * @since 2.0.0
     */
    public float pitch(Random random) {
        // Effectively generates a random number between -1F and 1F.
        final float randomness = (random.nextFloat() - 0.5F) * 2F;

        return this.pitch() + (randomness * this.pitchVariance());
    }

    /**
     * Plays a sound at the provided position within the world.
     *
     * @param world The world.
     * @param x The X position.
     * @param y The Y position.
     * @param z The Z position.
     *
     * @since 2.0.0
     */
    public void play(World world, double x, double y, double z) {
        final float pitch = this.pitch(world.getRandom());

        world.playSound(null, x, y, z, this.event(), this.category(), this.volume(), pitch);
    }

    /**
     * Plays a sound at the provided position within the world.
     *
     * @param world The world.
     * @param position The position.
     *
     * @since 2.0.0
     */
    public void play(World world, Vec3d position) {
        this.play(world, position.getX(), position.getY(), position.getZ());
    }

    /**
     * Plays a sound centered on the provided entity.
     *
     * @param world The world.
     * @param entity The entity.
     *
     * @since 2.0.0
     */
    public void play(World world, Entity entity) {
        final float pitch = this.pitch(world.getRandom());

        world.playSoundFromEntity(null, entity, this.event(), this.category(), this.volume(), pitch);
    }

    /**
     * Plays a sound centered on the provided entity.
     *
     * @param world The world.
     * @param blockPos The position.
     * @param useDistance Whether to use distance to determine if the sound should be played.
     *
     * @since 2.0.0
     */
    public void play(World world, BlockPos blockPos, boolean useDistance) {
        final float pitch = this.pitch(world.getRandom());

        world.playSoundAtBlockCenter(blockPos, this.event(), this.category(), this.volume(), pitch, useDistance);
    }

}
