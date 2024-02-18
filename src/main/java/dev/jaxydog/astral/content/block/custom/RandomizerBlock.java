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

package dev.jaxydog.astral.content.block.custom;

import dev.jaxydog.astral.content.block.CustomBlock;
import dev.jaxydog.astral.datagen.TagGenerator;
import dev.jaxydog.astral.register.Registered.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class RandomizerBlock extends CustomBlock implements Generated {

    /** Stores the active generated value until the `retain_state` property is false */
    public static final IntProperty ACTIVE_VALUE = IntProperty.of("active_value", 0, 15);
    /** Tells the block to reset its generated output when set to false */
    public static final BooleanProperty RETAIN_STATE = BooleanProperty.of("retain_state");
    /** Whether the block should be in "boolean mode", where it outputs either 0 or 15 at random */
    public static final BooleanProperty BOOLEAN_MODE = BooleanProperty.of("boolean_mode");

    public RandomizerBlock(String rawId, Settings settings) {
        super(rawId, settings);

        this.setDefaultState(this.stateManager.getDefaultState()
            .with(ACTIVE_VALUE, 0)
            .with(BOOLEAN_MODE, false)
            .with(RETAIN_STATE, false));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(ACTIVE_VALUE, BOOLEAN_MODE, RETAIN_STATE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(
        BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit
    ) {
        if (!player.canModifyBlocks()) return ActionResult.PASS;

        final BlockState toggled = state.cycle(BOOLEAN_MODE).with(RETAIN_STATE, false);

        world.setBlockState(pos, toggled);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, toggled));

        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (!world.isReceivingRedstonePower(pos)) {
            this.discardValue(state, world, pos);

            return 0;
        }

        if (!state.get(RETAIN_STATE)) this.generateValue(state, world, pos);

        return state.get(ACTIVE_VALUE);
    }

    /** Discards the block's active value */
    private void discardValue(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(RETAIN_STATE, false));
    }

    /** Generates a new active block value */
    private void generateValue(BlockState state, World world, BlockPos pos) {
        final int value;

        if (state.get(BOOLEAN_MODE)) {
            value = world.getRandom().nextBoolean() ? 0 : 15;
        } else {
            value = world.getRandom().nextBetween(0, 15);
        }

        world.setBlockState(pos, state.with(ACTIVE_VALUE, value).with(RETAIN_STATE, true));
    }

    @Override
    public void generate() {
        TagGenerator.getInstance().generate(BlockTags.PICKAXE_MINEABLE, b -> b.add(this));
    }

}
