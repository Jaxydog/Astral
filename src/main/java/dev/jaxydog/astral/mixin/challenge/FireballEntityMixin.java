package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireballEntity.class)
public abstract class FireballEntityMixin extends AbstractFireballEntity {

	@Unique
	private static final float MAX_POWER = 25F;

	public FireballEntityMixin(
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
