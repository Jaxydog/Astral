package dev.jaxydog.mixin.challenge;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.mob.WardenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(SonicBoomTask.class)
public abstract class SonicBoomTaskMixin extends MultiTickTask<WardenEntity> {

    public SonicBoomTaskMixin(
        Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int minRunTime, int maxRunTime
    ) {
        super(requiredMemoryState, minRunTime, maxRunTime);
    }

    @ModifyArg(
        method = "method_43265(Lnet/minecraft/entity/mob/WardenEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
        )
    )
    private static float scaleDamage(float damage, @Local(argsOnly = true) WardenEntity entity) {
        if (!MobChallengeUtil.shouldScale(entity)) return damage;

        final double additive = MobChallengeUtil.getAttackAdditive(entity.getWorld());

        return damage + (float) MobChallengeUtil.getScaledAdditive(entity, additive);
    }

}
