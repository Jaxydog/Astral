package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
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
		if (this.getOwner() != null && !MobChallengeUtil.shouldScale(this.getOwner())) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this.getOwner(), additive);

		return damage + (float) scaled;
	}

}
