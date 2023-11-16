package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements most of the mob challenge system's attack changes */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

	/** Returns the mixin's 'this' instance */
	private MobEntity self() {
		return (MobEntity) (Object) this;
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

		final MobEntity self = this.self();

		if (!MobChallengeUtil.isEnabled(self.getWorld())) {
			return attack;
		} else {
			final double additive = MobChallengeUtil.getAttackAdditive(self.getWorld());
			final double scaled = MobChallengeUtil.getScaledAdditive(self, additive);

			return attack + (float) scaled;
		}
	}

}
