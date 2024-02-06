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

package dev.jaxydog.content.power.condition;

import dev.jaxydog.content.power.CustomCondition;
import dev.jaxydog.content.power.CustomConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableData.Instance;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Extension of {@code origins:block_in_radius} that ensures that the detected block is not obstructed from the player's
 * current position.
 *
 * @author Jaxydog
 */
public class UnobstructedBlockInRadiusCondition extends CustomCondition<Entity> {

    public UnobstructedBlockInRadiusCondition(String rawId) {
        super(rawId);
    }

    /**
     * Performs a simple raycast between two start and end block positions, returning whether a block is within the
     * path.
     *
     * @param world The current world.
     * @param start The starting position.
     * @param end The ending position.
     * @param stepSize The size of a single step along the ray.
     *
     * @return Whether a block is within the path.
     */
    private boolean simpleRaycast(World world, BlockPos start, BlockPos end, double stepSize) {
        final Vec3d startCenter = start.toCenterPos();
        final Vec3d endCenter = end.toCenterPos();
        final Vec3d step = endCenter.subtract(startCenter).normalize().multiply(stepSize);
        final Set<BlockPos> checkedBlocks = new ObjectOpenHashSet<>((int) Math.ceil(startCenter.distanceTo(endCenter)));
        final double endDistance = startCenter.distanceTo(endCenter);

        // Ignore the two blocks themselves.
        checkedBlocks.add(start);
        checkedBlocks.add(end);

        Vec3d position = startCenter;

        // Ensure the ray distance is not exceeded.
        while (startCenter.distanceTo(position) < endDistance) {
            position = position.add(step);

            final BlockPos pos = BlockPos.ofFloored(position);

            if (checkedBlocks.contains(pos)) continue;

            final CachedBlockPosition cached = new CachedBlockPosition(world, pos, true);
            final VoxelShape shape = cached.getBlockState().getCollisionShape(world, pos);

            if (!shape.isEmpty()) {
                // Calculate the step's position within the block.
                final Vec3d relative = Vec3d.of(pos).relativize(position);
                final Box bounds = shape.getBoundingBox();

                if (bounds.contains(relative)) return true;
            }

            checkedBlocks.add(pos);
        }

        return false;
    }

    @Override
    public boolean check(Instance data, Entity entity) {
        final Shape shape = data.get("shape");
        final int radius = data.getInt("radius");
        final double stepSize = data.getDouble("step_size");
        final Predicate<CachedBlockPosition> blockCondition = data.get("block_condition");
        final Comparison comparison = data.get("comparison");
        final int compareTo = data.getInt("compare_to");

        final World world = entity.getWorld();
        final BlockPos entityPos = entity.getBlockPos();
        // Exits early if there are more blocks than expected, or if the count quota is reached.
        final int stopAt = switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> compareTo;
            default -> -1;
        };

        int count = 0;

        for (final BlockPos blockPos : Shape.getPositions(entityPos, shape, radius)) {
            final CachedBlockPosition cached = new CachedBlockPosition(world, blockPos, true);

            // Skip if the block condition fails or if there is a block in the way.
            if (!blockCondition.test(cached) || simpleRaycast(world, entityPos, blockPos, stepSize)) continue;

            count += 1;

            if (count == stopAt) break;
        }

        return comparison.compare(count, compareTo);
    }

    @Override
    public CustomConditionFactory<Entity> factory() {
        return new CustomConditionFactory<>(
            "unobstructed_block_in_radius",
            new SerializableData().add("block_condition", ApoliDataTypes.BLOCK_CONDITION)
                .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
                .add("radius", SerializableDataTypes.INT)
                .add("step_size", SerializableDataTypes.DOUBLE, 0.125D) // Should be reasonably accurate.
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.INT, 1),
            this::check
        );
    }

    @Override
    public Registry<ConditionFactory<Entity>> registry() {
        return ApoliRegistries.ENTITY_CONDITION;
    }

}
