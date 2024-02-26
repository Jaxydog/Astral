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

package dev.jaxydog.astral.utility;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.AstralGamerules;
import dev.jaxydog.astral.utility.injected.AstralLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Provides utility methods for dealing with mod challenge scaling. */
@NonExtendable
public interface MobChallengeUtil {

    /** The block position used as the world origin. */
    BlockPos ORIGIN = new BlockPos(0, 63, 0);
    /** An NBT key that tells an entity to ignore challenge scaling. */
    String IGNORE_KEY = "IgnoreChallengeScaling";
    /** An NBT key that tells an entity to force challenge scaling. */
    String FORCE_KEY = "ForceChallengeScaling";
    /** A tag that determines which entities are scaled. */
    TagKey<EntityType<?>> SCALED_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, Astral.getId("challenge"));

    /** Determines whether a given entity should have scaling applied */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean shouldScale(Entity entity) {
        // Ensure the entity is living firstly.
        return entity instanceof final LivingEntity living
            // Check is scaling is being forced.
            && (((AstralLivingEntity) living).astral$forcesChallengeScaling()
            // Check if scaling is enabled.
            || (isEnabled(living.getWorld())
            // Check if the entity is in the scaling tag.
            && living.getType().isIn(SCALED_ENTITIES)
            // Check if the entity ignores scaling.
            && !((AstralLivingEntity) living).astral$ignoresChallengeScaling()
            // Check if the entity is tamed.
            && (!(living instanceof final TameableEntity tamable) || !tamable.isTamed())));
    }

    /** Returns whether mob challenge scaling is enabled. */
    static boolean isEnabled(World world) {
        return world.getGameRules().getBoolean(AstralGamerules.CHALLENGE_ENABLED);
    }

    /** Returns the world's configured attack additive value. */
    static double getAttackAdditive(World world) {
        return world.getGameRules().get(AstralGamerules.CHALLENGE_ATTACK_ADDITIVE).get();
    }

    /** Returns the world's configured health additive value. */
    static double getHealthAdditive(World world) {
        return world.getGameRules().get(AstralGamerules.CHALLENGE_HEALTH_ADDITIVE).get();
    }

    /** Returns a statistic additive that has been scaled using the mob challenge configuration. */
    static double getScaledAdditive(Entity entity, double additive) {
        if (entity == null || entity.getWorld() == null) return additive;

        final int step = getChunkStep(entity.getWorld());
        final double distance = getSpawnDistance(entity);
        // Scale by chunks, not blocks.
        final double modifier = Math.max(0D, additive) * ((distance / 16D) / step);
        final boolean overworld = entity.getWorld().getRegistryKey().equals(World.OVERWORLD);

        return overworld ? modifier : modifier / 2D;
    }

    /** Returns the world's configured chunk step size. */
    static int getChunkStep(World world) {
        return Math.max(world.getGameRules().getInt(AstralGamerules.CHALLENGE_CHUNK_STEP), 1);
    }

    /** Returns the given entity's distance to the world spawn. */
    static double getSpawnDistance(Entity entity) {
        final boolean useOrigin = !entity.getWorld().getGameRules().get(AstralGamerules.CHALLENGE_USE_WORLDSPAWN).get();
        final BlockPos center = useOrigin ? ORIGIN : entity.getWorld().getSpawnPos();

        return Math.sqrt(entity.getBlockPos().getSquaredDistance(center));
    }

}
