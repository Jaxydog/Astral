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

import dev.jaxydog.astral.content.block.custom.DyeableAmethystBlock;
import dev.jaxydog.astral.content.block.custom.DyeableAmethystClusterBlock;
import dev.jaxydog.astral.content.block.custom.DyeableAmethystClusterBlock.Variant;
import dev.jaxydog.astral.content.block.custom.DyeableBuddingAmethystBlock;
import dev.jaxydog.astral.content.block.custom.RandomizerBlock;
import dev.jaxydog.astral.datagen.TagGenerator;
import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.DyedMap;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;

/** Contains definitions for all custom blocks */
public final class CustomBlocks extends ContentRegistrar {

    public static final DyedMap<DyeableAmethystBlock> DYEABLE_AMETHYST_BLOCKS = new DyedMap<>("amethyst_block",
        (rawId, color) -> new DyeableAmethystBlock(rawId, Settings.copy(Blocks.AMETHYST_BLOCK), color)
    );
    public static final DyedMap<DyeableBuddingAmethystBlock> DYEABLE_BUDDING_AMETHYST_BLOCKS = new DyedMap<>(
        "budding_amethyst",
        (rawId, color) -> new DyeableBuddingAmethystBlock(rawId, Settings.copy(Blocks.BUDDING_AMETHYST), color)
    );
    public static final DyedMap<DyeableAmethystClusterBlock> DYEABLE_AMETHYST_CLUSTERS = new DyedMap<>("amethyst_cluster",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.AMETHYST_CLUSTER).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.CLUSTER);
        }
    );
    public static final DyedMap<DyeableAmethystClusterBlock> DYEABLE_LARGE_AMETHYST_BUDS = new DyedMap<>("large_amethyst_bud",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.LARGE_AMETHYST_BUD).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.LARGE_BUD);
        }
    );
    public static final DyedMap<DyeableAmethystClusterBlock> DYEABLE_MEDIUM_AMETHYST_BUDS = new DyedMap<>("medium_amethyst_bud",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.MEDIUM_AMETHYST_BUD).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.MEDIUM_BUD);
        }
    );
    public static final DyedMap<DyeableAmethystClusterBlock> DYEABLE_SMALL_AMETHYST_BUDS = new DyedMap<>("small_amethyst_bud",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.SMALL_AMETHYST_BUD).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.SMALL_BUD);
        }
    );

    public static final RandomizerBlock RANDOMIZER = new RandomizerBlock("randomizer",
        Settings.copy(Blocks.IRON_BLOCK)
    );

    @Override
    public void generate() {
        super.generate();

        TagGenerator.getInstance().generate(DyeableAmethystBlock.AMETHYST_BLOCKS, b -> b.add(Blocks.AMETHYST_BLOCK));
        TagGenerator.getInstance()
            .generate(DyeableBuddingAmethystBlock.BUDDING_AMETHYSTS, b -> b.add(Blocks.BUDDING_AMETHYST));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.AMETHYST_CLUSTERS, b -> b.add(Blocks.AMETHYST_CLUSTER));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.LARGE_AMETHYST_BUDS, b -> b.add(Blocks.LARGE_AMETHYST_BUD));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.MEDIUM_AMETHYST_BUDS, b -> b.add(Blocks.MEDIUM_AMETHYST_BUD));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.SMALL_AMETHYST_BUDS, b -> b.add(Blocks.SMALL_AMETHYST_BUD));
    }

}
