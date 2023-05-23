package dev.jaxydog.astral.content.block;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/** An extension of a regular custom crop block that provides color support */
public abstract class ColoredCropBlock extends CustomCropBlock {

	public ColoredCropBlock(String rawId, Settings settings, Config config) {
		super(rawId, settings, config);
	}

	/** Returns the color that the block should render at the given index */
	protected abstract int getColor(BlockState state, BlockRenderView view, BlockPos pos, int index);

	@Override
	public void registerClient() {
		// This just ensures that registered blocks are actually colored
		// Calling this on the server will crash it, as the `ColorProviderRegistry` doesn't exist on that environment
		ColorProviderRegistry.BLOCK.register(this::getColor, this);
	}
}
