package dev.jaxydog.mixin.challenge;

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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SonicBoomTask.class)
public abstract class SonicBoomTaskMixin extends MultiTickTask<WardenEntity> {

	@Unique
	private static final double DAMAGE = 10D;
	@Shadow
	@Final
	private static int RUN_TIME;
	@Shadow
	@Final
	private static int SOUND_DELAY;

	public SonicBoomTaskMixin(
		Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int minRunTime, int maxRunTime
	) {
		super(requiredMemoryState, minRunTime, maxRunTime);
	}

	@Inject(
		method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/WardenEntity;J)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void keepRunningInject(ServerWorld serverWorld, WardenEntity entity, long l, CallbackInfo callbackInfo) {
		if (!MobChallengeUtil.shouldScale(entity)) return;

		final Brain<WardenEntity> brain = entity.getBrain();

		brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
			.ifPresent(target -> entity.getLookControl().lookAt(target.getPos()));

		final boolean hasSoundDelay = brain.hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_DELAY);
		final boolean hasSoundCooldown = brain.hasMemoryModule(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN);

		if (hasSoundDelay || hasSoundCooldown) return;

		brain.remember(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, RUN_TIME - SOUND_DELAY);
		brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET)
			.filter(entity::isValidTarget)
			.filter(target -> entity.isInRange(target, 15D, 20D))
			.ifPresent(target -> {
				final Vec3d head = entity.getPos().add(0D, 1.6F, 0D);
				final Vec3d feet = target.getEyePos().subtract(head);
				final Vec3d normal = feet.normalize();

				for (int multiplier = 1; multiplier < MathHelper.floor(feet.length()) + 7; ++multiplier) {
					final Vec3d pos = head.add(normal.multiply(multiplier));

					serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0D, 0D, 0D, 0D);
				}

				final double additive = MobChallengeUtil.getAttackAdditive(entity.getWorld());
				final double scaled = MobChallengeUtil.getScaledAdditive(entity, additive);
				final float damage = (float) (DAMAGE + scaled);

				entity.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3F, 1F);
				target.damage(serverWorld.getDamageSources().sonicBoom(entity), damage);

				final double resistance = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
				final double xz = 2.5D * (1D - resistance);
				final double y = 0.5D * (1D - resistance);

				target.addVelocity(normal.getX() * xz, normal.getY() * y, normal.getZ() * xz);
			});

		callbackInfo.cancel();
	}

}
