package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.RamImpactTask;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RamImpactTask.class)
public abstract class RamImpactTaskMixin extends MultiTickTask<GoatEntity> {

	@Unique
	private @Nullable GoatEntity entity;

	public RamImpactTaskMixin(
		Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int minRunTime, int maxRunTime
	) {
		super(requiredMemoryState, minRunTime, maxRunTime);
	}

	@Inject(
		method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/GoatEntity;J)V",
		at = @At("HEAD")
	)
	private void keepRunningCapture(ServerWorld w, GoatEntity entity, long l, CallbackInfo c) {
		this.entity = entity;
	}

	@ModifyArg(
		method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/GoatEntity;J)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
		),
		index = 1
	)
	private float keepRunningArgsInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this.entity)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.entity.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this.entity, additive);

		this.entity = null; // make sure we don't hold this forever

		return damage + (float) scaled;
	}

}
