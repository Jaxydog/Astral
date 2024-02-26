package dev.jaxydog.astral.mixin.challenge;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Hoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Hoglin.class)
public interface HoglinMixin {

    @ModifyArg(
        method = "tryAttack", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
    )
    )
    private static float tryAttackArgsInject(float damage, @Local(ordinal = 1, argsOnly = true) LivingEntity entity) {
        if (!MobChallengeUtil.shouldScale(entity)) return damage;

        final double additive = MobChallengeUtil.getAttackAdditive(entity.getWorld());

        return damage + (float) MobChallengeUtil.getScaledAdditive(entity, additive);
    }

}
