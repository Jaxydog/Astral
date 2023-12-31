package dev.jaxydog.content.block.color;

import dev.jaxydog.content.block.CustomBlock;
import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/** An extension of a regular custom block that provides color support */
public abstract class ColoredBlock extends CustomBlock implements Registered.Client {

	public ColoredBlock(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the color that the block should render as at the given index */
	public abstract int getColor(BlockState state, BlockRenderView view, BlockPos pos, int index);

	@Override
	public void registerClient() {
		ColorProviderRegistry.BLOCK.register(this::getColor, this);
	}

}
