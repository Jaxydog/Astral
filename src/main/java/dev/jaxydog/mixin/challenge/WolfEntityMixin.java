package dev.jaxydog.mixin.challenge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;

/** Implements the mob challenge system for wolves */
@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin {

	/** Returns the mixin's 'this' instance */
	private WolfEntity self() {
		return (WolfEntity) (Object) this;
	}

	@Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
	private void tryAttackInject(Entity target, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (((LivingEntityMixin) (Object) this).ignoreChallengeScaling) {
			return;
		}

		final WolfEntity self = this.self();
		final World world = self.getWorld();

		if (self.isTamed() || !MobChallengeUtil.isEnabled(world)) {
			return;
		}

		final double base = self.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		final double additive = MobChallengeUtil.getAttackAdditive(world);
		final double scaled = MobChallengeUtil.getScaledAdditive(self, additive);

		final DamageSource source = self.getDamageSources().mobAttack(self);
		final boolean applyEffects = target.damage(source, (float) (base + scaled));

		if (applyEffects) {
			self.applyDamageEffects(self, target);
		}

		callbackInfo.setReturnValue(applyEffects);
	}

}
