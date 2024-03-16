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

package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.register.Registered.Client;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Provides dynamic color support for the implementing block.
 *
 * @author Jaxydog
 * @since 1.7.0
 */
public interface Colored extends BlockConvertible, Client {

    /**
     * Returns the color for the given block at the provided layer index.
     *
     * @param state The block's current state.
     * @param view The block's rendering view.
     * @param position The block's current position.
     * @param index The color layer index.
     *
     * @return The block's color at the given index.
     *
     * @since 1.7.0
     */
    int getColor(BlockState state, BlockRenderView view, BlockPos position, int index);

    @Override
    default void registerClient() {
        ColorProviderRegistry.BLOCK.register(this::getColor, this.asBlock());
    }

}
