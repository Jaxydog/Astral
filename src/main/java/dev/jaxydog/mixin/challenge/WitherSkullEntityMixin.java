package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WitherSkullEntity.class)
public abstract class WitherSkullEntityMixin extends ExplosiveProjectileEntity {

	@Unique
	private static final float MAX_POWER = 25F;

	public WitherSkullEntityMixin(
		EntityType<? extends ExplosiveProjectileEntity> type,
		LivingEntity owner,
		double directionX,
		double directionY,
		double directionZ,
		World world
	) {
		super(type, owner, directionX, directionY, directionZ, world);
	}

	@ModifyArg(
		method = "onCollision", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"
	), index = 4
	)
	private float onCollisionArgsInject(float power) {
		if (this.getOwner() == null || !MobChallengeUtil.shouldScale(this.getOwner())) return power;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

		return Math.min(power + (float) (scaled / 10D), MAX_POWER);
	}

	@ModifyArg(
		method = "onEntityHit", at = @At(
		value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	), index = 1
	)
	private float onEntityHitArgsInject(float damage) {
		if (this.getOwner() == null || !MobChallengeUtil.shouldScale(this.getOwner())) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this.getOwner(), additive);
	}

}
