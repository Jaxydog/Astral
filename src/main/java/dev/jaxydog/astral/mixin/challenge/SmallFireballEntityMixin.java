package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SmallFireballEntity.class)
public abstract class SmallFireballEntityMixin extends AbstractFireballEntity {

    public SmallFireballEntityMixin(
        EntityType<? extends AbstractFireballEntity> entityType,
        LivingEntity livingEntity,
        double d,
        double e,
        double f,
        World world
    ) {
        super(entityType, livingEntity, d, e, f, world);
    }

    @ModifyArg(
        method = "onEntityHit", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
    ), index = 1
    )
    private float onEntityHitArgsInject(float damage) {
        if (this.getOwner() != null && !MobChallengeUtil.shouldScale(this.getOwner())) return damage;

        final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
        final double scaled = MobChallengeUtil.getScaledAdditive(this.getOwner(), additive);

        return damage + (float) scaled;
    }

}
