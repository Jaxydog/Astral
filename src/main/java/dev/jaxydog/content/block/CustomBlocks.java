package dev.jaxydog.content.block;

import dev.jaxydog.content.block.custom.DyedAmethystBlock;
import dev.jaxydog.content.block.custom.DyedAmethystClusterBlock;
import dev.jaxydog.content.block.custom.RandomizerBlock;
import dev.jaxydog.utility.DyeableMap;
import dev.jaxydog.utility.register.ContentContainer;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;

/** Contains definitions for all custom blocks */
public final class CustomBlocks extends ContentContainer {

	public static final DyeableMap<DyedAmethystBlock> DYED_AMETHYST_BLOCKS = new DyeableMap<>("amethyst_block",
		(rawId, color) -> new DyedAmethystBlock(rawId, Settings.copy(Blocks.AMETHYST_BLOCK))
	);
	public static final DyeableMap<DyedAmethystClusterBlock> DYED_AMETHYST_CLUSTER_BLOCKS = new DyeableMap<>(
		"amethyst_cluster",
		(rawId, _color) -> new DyedAmethystClusterBlock(rawId, Settings.copy(Blocks.AMETHYST_CLUSTER))
	);

	public static final RandomizerBlock RANDOMIZER = new RandomizerBlock("randomizer",
		Settings.copy(Blocks.IRON_BLOCK)
	);

}
