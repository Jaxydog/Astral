package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Hoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Hoglin.class)
public interface HoglinMixin {

	@ModifyArgs(
		method = "tryAttack", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	)
	)
	private static void tryAttackArgsInject(Args args) {
		final DamageSource source = args.get(0);

		if (!MobChallengeUtil.shouldScale(source.getAttacker())) return;

		final double additive = MobChallengeUtil.getAttackAdditive(source.getAttacker().getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(source.getAttacker(), additive);

		args.set(1, args.<Float>get(1) + (float) scaled);
	}

}
