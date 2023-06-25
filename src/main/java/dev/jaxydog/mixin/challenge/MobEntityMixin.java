package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.ChallengeUtil;
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
		MobEntity self = this.self();

		if (!ChallengeUtil.isEnabled(self.getWorld()))
			return attack;

		int additive = ChallengeUtil.getAttackAdditive(self.getWorld());
		double modifier = ChallengeUtil.getChallengeModifier(self, additive);

		return attack + (float) modifier;
	}

}
