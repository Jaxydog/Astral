package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.content.block.custom.DyedAmethystBlock;
import dev.jaxydog.astral.content.block.custom.DyedAmethystClusterBlock;
import dev.jaxydog.astral.utility.AutoRegister;
import dev.jaxydog.astral.utility.DyeableSet;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

/** Contains definitions for all custom blocks */
@AutoRegister
public class CustomBlocks {

	public static final DyeableSet<DyedAmethystBlock> DYED_AMETHYST_BLOCK = new DyeableSet<>(
		"amethyst_block",
		(id, color) ->
			new DyedAmethystBlock(
				id,
				Settings.of(Material.AMETHYST).sounds(BlockSoundGroup.AMETHYST_BLOCK).strength(1.5F).requiresTool()
			)
	);
	public static final DyeableSet<DyedAmethystClusterBlock> DYED_AMETHYST_CLUSTER = new DyeableSet<>(
		"amethyst_cluster",
		(id, color) ->
			new DyedAmethystClusterBlock(
				id,
				Settings
					.of(Material.AMETHYST)
					.luminance(state -> 5)
					.nonOpaque()
					.sounds(BlockSoundGroup.AMETHYST_CLUSTER)
					.strength(1.5F)
					.requiresTool()
			)
	);
}
