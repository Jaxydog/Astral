package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.content.block.custom.DyedAmethystBlock;
import dev.jaxydog.astral.content.block.custom.DyedAmethystClusterBlock;
import dev.jaxydog.astral.utility.AutoRegister;
import dev.jaxydog.astral.utility.DyeableHelper;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

/** Contains definitions for all custom blocks */
@AutoRegister
public class CustomBlocks {

	public static final DyeableHelper<DyedAmethystBlock> DYED_AMETHYST_BLOCK = new DyeableHelper<>(
		"amethyst_block",
		Settings.of(Material.AMETHYST).sounds(BlockSoundGroup.AMETHYST_BLOCK).strength(1.5F).requiresTool(),
		DyedAmethystBlock::new
	);
	public static final DyeableHelper<DyedAmethystClusterBlock> DYED_AMETHYST_CLUSTER = new DyeableHelper<>(
		"amethyst_cluster",
		Settings
			.of(Material.AMETHYST)
			.luminance(state -> 5)
			.nonOpaque()
			.sounds(BlockSoundGroup.AMETHYST_CLUSTER)
			.strength(1.5F)
			.requiresTool(),
		DyedAmethystClusterBlock::new
	);
}
