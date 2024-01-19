package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuardianEntity.class)
public abstract class GuardianEntityMixin extends HostileEntity {

	protected GuardianEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyArg(
		method = "damage", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
	), index = 1
	)
	private float damageArgsInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
	}

}
