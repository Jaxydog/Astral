package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LlamaSpitEntity.class)
public abstract class LlamaSpitEntityMixin extends ProjectileEntity {

    public LlamaSpitEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(
        method = "onEntityHit", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
    ), index = 1
    )
    private float onEntityHitArgsInject(float damage) {
        if (!MobChallengeUtil.shouldScale(this)) return damage;

        final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

        return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
    }

}
