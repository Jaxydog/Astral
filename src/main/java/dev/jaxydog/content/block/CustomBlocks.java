package dev.jaxydog.content.block;

import dev.jaxydog.content.block.custom.DyedAmethystBlock;
import dev.jaxydog.content.block.custom.DyedAmethystClusterBlock;
import dev.jaxydog.content.block.custom.RandomizerBlock;
import dev.jaxydog.utility.DyeableSet;
import dev.jaxydog.utility.register.ContentContainer;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock.Settings;

/** Contains definitions for all custom blocks */
public final class CustomBlocks extends ContentContainer {

        public static final DyeableSet<DyedAmethystBlock> DYED_AMETHYST_BLOCK_SET =
                        new DyeableSet<>("amethyst_block",
                                        (rawId, color) -> new DyedAmethystBlock(rawId,
                                                        Settings.copy(Blocks.AMETHYST_BLOCK)));
        public static final DyeableSet<DyedAmethystClusterBlock> DYED_AMETHYST_CLUSTER_BLOCK_SET =
                        new DyeableSet<>("amethyst_cluster",
                                        (rawId, color) -> new DyedAmethystClusterBlock(rawId,
                                                        Settings.copy(Blocks.AMETHYST_CLUSTER)));

        public static final RandomizerBlock RANDOMIZER =
                        new RandomizerBlock("randomizer", Settings.copy(Blocks.COMMAND_BLOCK));

}
