package dev.jaxydog.content.block.custom;

import dev.jaxydog.content.block.CustomBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Implements dyed amethyst blocks */
public class DyedAmethystBlock extends CustomBlock {

	public DyedAmethystBlock(String rawId, Settings settings) {
		super(rawId, settings);
	}

	private void playChimeSound(World world, BlockPos pos, SoundEvent sound) {
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 0.5f + world.random.nextFloat() * 1.2f);
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (!world.isClient) {
			final BlockPos position = hit.getBlockPos();

			this.playChimeSound(world, position, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
			this.playChimeSound(world, position, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
		}
	}

}
