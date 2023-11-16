package dev.jaxydog.mixin.challenge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.world.World;

/** Implements the mob challenge system for polar bears */
@Mixin(PolarBearEntity.class)
public abstract class PolarBearEntityMixin extends AnimalEntity implements Angerable {

	protected PolarBearEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyArg(method = "tryAttack", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
			index = 1)
	private float tryAttackArgsInject(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) {
			return damage;
		}

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

		return damage + (float) scaled;
	}

}
