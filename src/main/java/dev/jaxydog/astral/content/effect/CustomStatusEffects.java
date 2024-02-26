package dev.jaxydog.astral.content.effect;

import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.utility.injected.AstralLightningEntity;
import io.github.apace100.origins.util.Scheduler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.math.random.Random;

public final class CustomStatusEffects extends ContentRegistrar {

    public static final CustomStatusEffect SINISTER = new CustomStatusEffect("sinister",
        StatusEffectCategory.HARMFUL,
        0xE43727
    ) {
        private final Scheduler scheduler = new Scheduler();

        @SuppressWarnings("RedundantCast")
        @Override
        public synchronized void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            switch (Math.min(entity.getRandom().nextBetween(0, amplifier + 2), 5)) {
                case 0 -> entity.damage(entity.getWorld().getDamageSources().magic(), 2 * (amplifier + 2));
                case 1 -> {
                    entity.setFrozenTicks(0);
                    entity.setOnFireFor(amplifier + 2);
                }
                case 2 -> {
                    entity.extinguishWithSound();
                    entity.setFrozenTicks(60 * (amplifier + 2));
                }
                case 3 -> {
                    final Random random = entity.getWorld().getRandom();
                    final double x = (random.nextDouble() - 0.5) * 2;
                    final double y = amplifier * 1.5;
                    final double z = (random.nextDouble() - 0.5) * 2;

                    entity.setVelocity(x, y, z);
                    entity.velocityModified = true;
                }
                case 4 -> {
                    if (entity.isSubmergedInWater()) {
                        final Random random = entity.getWorld().getRandom();
                        final double x = (random.nextDouble() - 0.5) * 2;
                        final double y = -amplifier * 0.75;
                        final double z = (random.nextDouble() - 0.5) * 2;

                        // Attempt to drown the player if they're swimming.
                        entity.setAir(0);
                        entity.setVelocity(x, y, z);
                        entity.velocityModified = true;
                    } else if (!entity.getWorld().isClient()) {
                        // Otherwise spawn a pufferfish on them.
                        final PufferfishEntity pufferfish = new PufferfishEntity(EntityType.PUFFERFISH,
                            entity.getWorld()
                        );

                        pufferfish.setPosition(entity.getSyncedPos());

                        entity.getWorld().spawnEntity(pufferfish);
                    }
                }
                case 5 -> {
                    if (entity.getWorld().isClient()) return;

                    // Strike them with lightning.
                    final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.getWorld());

                    lightning.setPosition(entity.getSyncedPos());
                    ((AstralLightningEntity) lightning).astral$setPreservesItems(true);

                    entity.getWorld().spawnEntity(lightning);
                }
            }

            if (amplifier > 0) {
                this.scheduler.queue(server -> entity.addStatusEffect(new StatusEffectInstance(SINISTER,
                    Math.min(20 * (amplifier), 200),
                    amplifier - 1
                )), 1);
            }
        }
    };

}
