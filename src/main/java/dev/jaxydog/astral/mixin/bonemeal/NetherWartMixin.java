package dev.jaxydog.astral.mixin.bonemeal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

/** Implements the `Fertilizable` interface for nether wart */
@Mixin(NetherWartBlock.class)
@Implements(@Interface(iface = Fertilizable.class, prefix = "impl$"))
public abstract class NetherWartMixin {

	public boolean impl$isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
		return state.get(NetherWartBlock.AGE) < 3;
	}

	public boolean impl$canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return state.get(NetherWartBlock.AGE) < 3;
	}

	public void impl$grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		var growth = random.nextInt(2) + 1;
		var current = state.get(NetherWartBlock.AGE);

		state = state.with(NetherWartBlock.AGE, Math.min(current + growth, 3));

		world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
	}
}
