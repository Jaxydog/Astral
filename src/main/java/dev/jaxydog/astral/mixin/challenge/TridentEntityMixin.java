package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {

	protected TridentEntityMixin(
		EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world
	) {
		super(type, owner, world);
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
