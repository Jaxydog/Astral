package dev.jaxydog.content.block;

import dev.jaxydog.content.block.custom.DyedAmethystBlock;
import dev.jaxydog.content.block.custom.DyedAmethystClusterBlock;
import dev.jaxydog.content.block.custom.RandomizerBlock;
import dev.jaxydog.register.ContentRegistrar;
import dev.jaxydog.utility.DyeableMap;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;

/** Contains definitions for all custom blocks */
public final class CustomBlocks extends ContentRegistrar {

	public static final DyeableMap<DyedAmethystBlock> DYED_AMETHYST_BLOCKS = new DyeableMap<>("amethyst_block",
		(rawId, color) -> new DyedAmethystBlock(rawId, color, Settings.copy(Blocks.AMETHYST_BLOCK))
	);
	public static final DyeableMap<DyedAmethystClusterBlock> DYED_AMETHYST_CLUSTER_BLOCKS = new DyeableMap<>(
		"amethyst_cluster",
		(rawId, color) -> new DyedAmethystClusterBlock(rawId, color, Settings.copy(Blocks.AMETHYST_CLUSTER))
	);

	public static final RandomizerBlock RANDOMIZER = new RandomizerBlock("randomizer",
		Settings.copy(Blocks.IRON_BLOCK)
	);

}
