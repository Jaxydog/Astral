package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PufferfishEntity.class)
public abstract class PufferfishEntityMixin extends FishEntity {

	public PufferfishEntityMixin(EntityType<? extends FishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract int getPuffState();

	@Unique
	private float scale(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
	}

	@ModifyArg(
		method = "sting", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/mob/MobEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	)
	)
	private float scaleStringDamage(float damage) {
		return this.scale(damage);
	}

	@ModifyArg(
		method = "onPlayerCollision", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/player/PlayerEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	)
	)
	private float scaleCollisionDamage(float damage) {
		return this.scale(damage);
	}

}
