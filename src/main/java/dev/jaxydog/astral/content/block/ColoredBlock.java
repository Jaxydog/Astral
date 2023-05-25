package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.utility.register.Registerable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/** An extension of a regular custom block that provides color support */
public abstract class ColoredBlock extends CustomBlock implements Registerable.Client {

	public ColoredBlock(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the color that the block should render at the given index */
	protected abstract int getColor(BlockState state, BlockRenderView view, BlockPos pos, int index);

	@Override
	public void registerClient() {
		Client.super.registerClient();
		// This just ensures that registered blocks are actually colored
		// Calling this on the server will crash it, as the `ColorProviderRegistry` doesn't exist on that environment
		ColorProviderRegistry.BLOCK.register(this::getColor, this);
	}
}
