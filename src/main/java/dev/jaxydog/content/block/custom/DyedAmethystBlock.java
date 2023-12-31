package dev.jaxydog.content.block.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.content.block.CustomBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Implements dyed amethyst blocks */
public class DyedAmethystBlock extends CustomBlock {

	public static final TagKey<Item> AMETHYST_BLOCKS = TagKey.of(Registries.ITEM.getKey(),
		Astral.getId("amethyst_blocks")
	);

	protected final DyeColor color;

	public DyedAmethystBlock(String rawId, DyeColor color, Settings settings) {
		super(rawId, settings);

		this.color = color;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (!world.isClient) {
			final BlockPos position = hit.getBlockPos();

			this.playChimeSound(world, position, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
			this.playChimeSound(world, position, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
		}
	}

	private void playChimeSound(World world, BlockPos pos, SoundEvent sound) {
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 0.5f + world.random.nextFloat() * 1.2f);
	}

}
