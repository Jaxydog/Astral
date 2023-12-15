package dev.jaxydog.mixin.challenge;

import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(SonicBoomTask.class)
public abstract class SonicBoomTaskMixin extends MultiTickTask<WardenEntity> {

	@Shadow
	@Final
	private static int RUN_TIME;

	@Shadow
	@Final
	private static int SOUND_DELAY;

	private static final double DAMAGE = 10D;

	public SonicBoomTaskMixin(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int minRunTime,
		int maxRunTime) {
		super(requiredMemoryState, minRunTime, maxRunTime);
	}

	@Inject(method = "keepRunning", at = @At("HEAD"), cancellable = true)
	private void keepRunningInject(ServerWorld serverWorld, WardenEntity entity, long l, CallbackInfo callbackInfo) {
		if (!MobChallengeUtil.shouldScale(entity)) {
			return;
		}

		final Brain<WardenEntity> brain = entity.getBrain();

		brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
			.ifPresent(target -> entity.getLookControl().lookAt(target.getPos()));

		final boolean hasSoundDelay = brain.hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_DELAY);
		final boolean hasSoundCooldown = brain.hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN);

		if (hasSoundDelay || hasSoundCooldown) {
			return;
		}

		brain.remember(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, RUN_TIME - SOUND_DELAY);

		brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
			.filter(entity::isValidTarget)
			.filter(target -> entity.isInRange(target, 15.0, 20.0))
			.ifPresent(target -> {
				final Vec3d head = entity.getPos().add(0.0, 1.6f, 0.0);
				final Vec3d feet = target.getEyePos().subtract(head);
				final Vec3d normal = feet.normalize();

				for (int i = 1; i < MathHelper.floor(feet.length()) + 7; ++i) {
					final Vec3d pos = head.add(normal.multiply(i));

					serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
				}

				final double additive = MobChallengeUtil.getAttackAdditive(entity.getWorld());
				final double scaled = MobChallengeUtil.getScaledAdditive(entity, additive);
				final float damage = (float) (DAMAGE + scaled);

				entity.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0f, 1.0f);
				target.damage(serverWorld.getDamageSources().sonicBoom(entity), damage);

				final double xz = 2.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
				final double y = 0.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));

				target.addVelocity(normal.getX() * xz, normal.getY() * y, normal.getZ() * xz);
			});

		callbackInfo.cancel();
	}

}
