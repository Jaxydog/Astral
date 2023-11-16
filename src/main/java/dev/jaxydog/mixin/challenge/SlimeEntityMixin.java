package dev.jaxydog.mixin.challenge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;

/** Implements the mob challenge system for slimes */
@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin {

	/** Returns the mixin's 'this' instance */
	private SlimeEntity self() {
		return (SlimeEntity) (Object) this;
	}

	@Inject(method = "getDamageAmount", at = @At("HEAD"), cancellable = true)
	private void getDamageAmountMixin(LivingEntity target,
			CallbackInfoReturnable<Float> callbackInfo) {
		if (((LivingEntityMixin) (Object) this).ignoreChallengeScaling) {
			return;
		}

		final SlimeEntity self = this.self();
		final World world = self.getWorld();

		if (!MobChallengeUtil.isEnabled(world)) {
			return;
		}

		final double base = self.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		final double additive = MobChallengeUtil.getAttackAdditive(world);
		final double scaled = MobChallengeUtil.getScaledAdditive(self, additive);

		callbackInfo.setReturnValue((float) (base + scaled));
	}

}
