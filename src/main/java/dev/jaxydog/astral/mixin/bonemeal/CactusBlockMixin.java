package dev.jaxydog.astral.mixin.bonemeal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

/** Implements the `Fertilizable` interface for cactus */
@Mixin(CactusBlock.class)
@Implements(@Interface(iface = Fertilizable.class, prefix = "impl$"))
public abstract class CactusBlockMixin {

	/** Configures the maximum fertilizable height of a cactus */
	private static final int MAX_HEIGHT = 3;
	/** Configures how likely a cactus is to grow when fertilizing it */
	private static final float GROW_CHANCE = 2.0F / 3.0F;
	/** Configures how likely a cactus is to grow an extra time when fertilizing it */
	private static final float BONUS_CHANCE = 1.0F / 3.0F;

	/** Returns the top-most block of the cactus */
	private BlockPos getTop(WorldView world, BlockPos current) {
		while (true) {
			var above = current.up();

			if (world.getBlockState(above).getBlock() instanceof CactusBlock) {
				current = above;
			} else {
				return current;
			}
		}
	}

	/** Returns the height of the cactus, counting downwards from the given position */
	private int getHeight(WorldView world, BlockPos pos, boolean fromTop) {
		var height = 1;

		if (fromTop) pos = this.getTop(world, pos);

		while (world.getBlockState(pos.down(height)).getBlock() instanceof CactusBlock) {
			height += 1;
		}

		return height;
	}

	public boolean impl$isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
		var self = (CactusBlock) (Object) this;
		var top = this.getTop(world, pos);

		if (!world.getBlockState(top).isAir()) return false;
		if (!self.canPlaceAt(world.getBlockState(top), world, top)) return false;

		return this.getHeight(world, top, true) < CactusBlockMixin.MAX_HEIGHT;
	}

	public boolean impl$canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	public void impl$grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		var self = (CactusBlock) (Object) this;
		var top = this.getTop(world, pos).up();

		if (random.nextFloat() > CactusBlockMixin.GROW_CHANCE) return;

		world.setBlockState(top, Blocks.CACTUS.getDefaultState());

		if (this.getHeight(world, top, false) >= CactusBlockMixin.MAX_HEIGHT) return;

		top = top.up();

		if (!self.canPlaceAt(world.getBlockState(top), world, top)) return;
		if (random.nextFloat() > CactusBlockMixin.BONUS_CHANCE) return;

		world.setBlockState(top, Blocks.CACTUS.getDefaultState());
	}
}
