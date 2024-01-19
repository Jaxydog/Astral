package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity implements Monster {

	protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyArg(
		method = "launchLivingEntities", at = @At(
		value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	), index = 1
	)
	private float launchLivingEntitiesArgsInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
	}

	@ModifyArg(
		method = "damageLivingEntities", at = @At(
		value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	), index = 1
	)
	private float damageLivingEntitiesArgsInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
	}

}
