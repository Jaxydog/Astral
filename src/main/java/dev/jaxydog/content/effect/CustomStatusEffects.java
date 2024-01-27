package dev.jaxydog.content.effect;

import dev.jaxydog.register.ContentRegistrar;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public final class CustomStatusEffects extends ContentRegistrar {

	public static final CustomStatusEffect SINISTER = new CustomStatusEffect("sinister",
		StatusEffectCategory.HARMFUL,
		0xE43727
	) {
		private final WeightedList<Runnable> effects = new WeightedList<>();

		@Override
		public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
			super.onApplied(entity, attributes, amplifier);

			effects.add(() -> {
				entity.setFrozenTicks(0);
				entity.setOnFireFor(amplifier + 1);
			}, 1);
			effects.add(() -> {
				entity.extinguishWithSound();
				entity.setFrozenTicks(60 * (amplifier + 2));
			}, 1);
			effects.add(() -> {
				final float damage = 2 * (amplifier + 2);

				entity.damage(entity.getWorld().getDamageSources().magic(), damage);
			}, 1);

			if (amplifier >= 1) {
				effects.add(() -> {
					final Random random = entity.getWorld().getRandom();

					entity.setVelocity((random.nextDouble() - 0.5) * 2,
						amplifier * 0.5,
						(random.nextDouble() - 0.5) * 2
					);
					entity.velocityModified = true;
				}, 1);
			}
			if (amplifier >= 2) {
				effects.add(() -> {
					final World world = entity.getWorld();
					final BlockPos pos = entity.getBlockPos().up();

					if (world.isAir(pos) && world.canSetBlock(pos)) {
						world.setBlockState(pos, Blocks.WATER.getDefaultState());
					}

					entity.damage(entity.getWorld().getDamageSources().drown(), amplifier);
					entity.setAir(0);
				}, 1);
			}
			if (amplifier >= 3) {
				effects.add(() -> {
					if (entity.getWorld().isClient()) return;

					final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.getWorld());

					lightning.setPosition(entity.getPos());

					entity.getWorld().spawnEntity(lightning);
				}, 1);
			}
		}

		@Override
		public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
			this.effects.shuffle();
			this.effects.stream().findFirst().ifPresent(Runnable::run);

			super.onRemoved(entity, attributes, amplifier);
		}
	};

}
