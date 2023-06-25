package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.ChallengeUtil;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements the mob challenge system for creepers */
@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

	/** Returns the mixin's 'this' instance */
	private CreeperEntity self() {
		return (CreeperEntity) (Object) this;
	}

	/** Modifies the creeper's explosion strength to account for mob challenge scaling */
	@ModifyVariable(method = "explode", at = @At("STORE"), ordinal = 0)
	private float powerVarInject(float power) {
		CreeperEntity self = this.self();

		if (!ChallengeUtil.isEnabled(self.getWorld()))
			return power;

		int additive = ChallengeUtil.getAttackAdditive(self.getWorld());
		double modifier = ChallengeUtil.getChallengeModifier(self, additive);

		return power + ((float) modifier / 5.0F);
	}

}
