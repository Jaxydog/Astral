package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.ChallengeUtil;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements most of the mob challenge system's attack changes */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

	/** Modifies the damage value within the `tryAttack` method to increase damage based on challenge scaling */
	@ModifyVariable(method = "tryAttack", at = @At("STORE"), ordinal = 0)
	private float injectModifiedAttack(float attack) {
		var self = (MobEntity) (Object) this;

		if (!ChallengeUtil.isEnabled(self.getWorld())) return attack;

		var additive = ChallengeUtil.getAttackAdditive(self.getWorld());
		var modifier = ChallengeUtil.getChallengeModifier(self, additive);

		return attack + (float) modifier;
	}
}
