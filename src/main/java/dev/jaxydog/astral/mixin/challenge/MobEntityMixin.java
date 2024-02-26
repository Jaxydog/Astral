package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Implements most of the mob challenge system's attack changes */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements Targeter {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(
        method = "tryAttack", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
    ), index = 1
    )
    private float tryAttackArgsInject(float damage) {
        if (!MobChallengeUtil.shouldScale(this)) return damage;

        final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

        return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
    }

}
