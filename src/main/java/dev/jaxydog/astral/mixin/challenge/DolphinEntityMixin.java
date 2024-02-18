package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DolphinEntity.class)
public class DolphinEntityMixin extends WaterCreatureEntity {

	protected DolphinEntityMixin(EntityType<? extends WaterCreatureEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyArg(
		method = "tryAttack", at = @At(
		value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	), index = 1
	)
	private float tryAttackInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
	}

}
