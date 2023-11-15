package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements the mob challenge system for creepers */
@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

	/** The maximum allowed explosion power. */
	private static final double MAX_POWER = 50.0D;

	/** Returns the mixin's 'this' instance */
	private final CreeperEntity self() {
		return (CreeperEntity) (Object) this;
	}

	/** Modifies the creeper's explosion strength to account for mob challenge scaling */
	@ModifyVariable(method = "explode", at = @At("STORE"), ordinal = 0)
	private float powerVarInject(float power) {
		final CreeperEntity self = this.self();

		if (!MobChallengeUtil.isEnabled(self.getWorld())) {
			return power;
		}

		final double additive = MobChallengeUtil.getAttackAdditive(self.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(self, additive);

		return (float) Math.max(power + (scaled / 10.0D), CreeperEntityMixin.MAX_POWER);
	}

}
