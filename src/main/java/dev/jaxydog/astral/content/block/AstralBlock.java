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

import net.minecraft.block.Block;

/**
 * An extension of an {@link Block} that provides commonly used functionality.
 * <p>
 * This is one of the various instances of already-provided wrapper classes for commonly used types.
 * <p>
 * In future code, you should prefer to extend this class over {@link Block} if at all possible.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 * @see Custom
 * @since 2.0.0
 */
public class AstralBlock extends Block implements Custom {

    /**
     * The block's identifier path used within the registration system.
     *
     * @since 2.0.0
     */
    private final String path;

    /**
     * Creates a new block using the given settings.
     *
     * @param path The block's identifier path.
     * @param settings The block's settings.
     *
     * @since 2.0.0
     */
    public AstralBlock(String path, Settings settings) {
        super(settings);

        this.path = path;
    }

    @Override
    public Block asBlock() {
        return this;
    }

    @Override
    public String getRegistryPath() {
        return this.path;
    }

}
