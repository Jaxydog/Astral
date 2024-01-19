package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.GuardianEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.entity.mob.GuardianEntity$FireBeamGoal")
public abstract class GuardianEntityFireBeamGoalMixin extends Goal {

	@Shadow
	@Final
	private GuardianEntity guardian;

	@ModifyArg(
		method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	), index = 1
	)
	private float tickArgsInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this.guardian)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.guardian.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this.guardian, additive);
	}

}
