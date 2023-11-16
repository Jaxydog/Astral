package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements most of the mob challenge system's attack changes */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements Targeter {

	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	/**
	 * Modifies the damage value within the `tryAttack` method to increase damage based on challenge
	 * scaling
	 */
	@ModifyVariable(method = "tryAttack", at = @At("STORE"), ordinal = 0)
	private float damageVarInject(float attack) {
		if (((LivingEntityMixin) (Object) this).ignoreChallengeScaling) {
			return attack;
		}
		if (!MobChallengeUtil.isEnabled(this.getWorld())) {
			return attack;
		}

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

		return attack + (float) scaled;
	}

}
