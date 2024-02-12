package dev.jaxydog.content.effect;

import dev.jaxydog.register.ContentRegistrar;
import dev.jaxydog.utility.injected.AstralLightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.random.Random;

public final class CustomStatusEffects extends ContentRegistrar {

    public static final CustomStatusEffect SINISTER = new CustomStatusEffect("sinister",
        StatusEffectCategory.HARMFUL,
        0xE43727
    ) {
        private final WeightedList<Runnable> effects = new WeightedList<>();

        @SuppressWarnings("RedundantCast")
        @Override
        public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            super.onApplied(entity, attributes, amplifier);

            this.effects.add(() -> {
                entity.setFrozenTicks(0);
                entity.setOnFireFor(amplifier + 1);
            }, 1);
            this.effects.add(() -> {
                entity.extinguishWithSound();
                entity.setFrozenTicks(60 * (amplifier + 2));
            }, 1);
            this.effects.add(() -> {
                final float damage = 2 * (amplifier + 2);

                entity.damage(entity.getWorld().getDamageSources().magic(), damage);
            }, 1);

            if (amplifier >= 1) {
                this.effects.add(() -> {
                    final Random random = entity.getWorld().getRandom();

                    entity.setVelocity((random.nextDouble() - 0.5) * 4,
                        amplifier * 0.5,
                        (random.nextDouble() - 0.5) * 4
                    );
                    entity.velocityModified = true;
                }, 1);
            }
            if (amplifier >= 2) {
                this.effects.add(() -> {
                    if (entity.isSubmergedInWater()) {
                        final Random random = entity.getWorld().getRandom();

                        entity.setAir(0);
                        entity.setVelocity((random.nextDouble() - 0.5) * 2,
                            amplifier * 0.5,
                            (random.nextDouble() - 0.5) * 2
                        );
                        entity.velocityModified = true;
                    } else if (!entity.getWorld().isClient()) {
                        final PufferfishEntity pufferfish = new PufferfishEntity(EntityType.PUFFERFISH,
                            entity.getWorld()
                        );

                        pufferfish.setPosition(entity.getSyncedPos());

                        entity.getWorld().spawnEntity(pufferfish);
                    }
                }, 1);
            }
            if (amplifier >= 3) {
                this.effects.add(() -> {
                    if (entity.getWorld().isClient()) return;

                    final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.getWorld());

                    lightning.setPosition(entity.getSyncedPos());
                    ((AstralLightningEntity) lightning).astral$setPreservesItems(true);

                    entity.getWorld().spawnEntity(lightning);
                }, 1);
            }
        }

        @Override
        public synchronized void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            this.effects.shuffle();
            this.effects.stream().findFirst().ifPresent(Runnable::run);

            super.onRemoved(entity, attributes, amplifier);

            if (amplifier > 0) entity.addStatusEffect(new StatusEffectInstance(SINISTER, 200, amplifier - 1));
        }
    };

}
