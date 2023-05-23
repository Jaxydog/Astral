package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.ChallengeHelper;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements the mob challenge system for creepers */
@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

	/** Modifies the creeper's explosion strength to account for mob challenge scaling */
	@ModifyVariable(method = "explode", at = @At("STORE"), ordinal = 0)
	private float injectModifiedPower(float power) {
		var self = (CreeperEntity) (Object) this;

		if (!ChallengeHelper.isEnabled(self.getWorld())) return power;

		var additive = ChallengeHelper.getAttackAdditive(self.getWorld());
		var modifier = ChallengeHelper.getChallengeModifier(self, additive);

		return power + ((float) modifier / 5.0F);
	}
}
