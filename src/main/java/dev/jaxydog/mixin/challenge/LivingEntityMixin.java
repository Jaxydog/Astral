package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Implements the mob challenge system's health changes */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	/**
	 * Stores whether or not the entity needs to reset its health; should only be true if the
	 * gamerules are updated or when the entity is first spawned
	 */
	private boolean shouldResetHealth = false;
	/**
	 * Stores the previously used health additive value to check whether the entity's health should
	 * be updated
	 */
	private double lastHealthAdditive = MobChallengeUtil.getHealthAdditive(this.self().getWorld());
	/**
	 * Stores the previously used chunk step value to check whether the entity's health should be
	 * updated
	 */
	private double lastChunkStep = MobChallengeUtil.getChunkStep(this.self().getWorld());

	/** Returns the mixin's 'this' instance */
	private final LivingEntity self() {
		return (LivingEntity) (Object) this;
	}

	/**
	 * Convenience method to sent the entity's current health to the given value without calling
	 * `getMaxHealth()`
	 */
	private final void setHealthData(float health) {
		this.self().getDataTracker().set(LivingEntityAccessor.getHealthTracker(),
				Math.max(0, health));
	}

	/** Provides a scaled maximum health value if mob challenge scaling is enabled */
	@Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
	private void getMaxHealthInject(CallbackInfoReturnable<Float> callbackInfo) {
		final LivingEntity living = this.self();

		if (!(living instanceof MobEntity self) || !MobChallengeUtil.isEnabled(self.getWorld())) {
			return;
		}

		final double additive = MobChallengeUtil.getHealthAdditive(self.getWorld());

		if (this.lastHealthAdditive != additive) {
			this.shouldResetHealth = true;
			this.lastHealthAdditive = additive;
		}

		final int chunkStep = MobChallengeUtil.getChunkStep(self.getWorld());

		if (this.lastChunkStep != chunkStep) {
			this.shouldResetHealth = true;
			this.lastChunkStep = chunkStep;
		}

		final double base = self.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
		final double scaled = MobChallengeUtil.getScaledAdditive(self, additive);

		callbackInfo.setReturnValue((float) (base + scaled));
	}

	/** Automatically updates an entity's maximum health if necessary */
	@Inject(method = "tick", at = @At("TAIL"))
	private void tickInject(CallbackInfo callbackInfo) {
		final LivingEntity self = this.self();

		if (!(self instanceof MobEntity) || self.getPos() == Vec3d.ZERO) {
			return;
		}

		final boolean enabled = !MobChallengeUtil.isEnabled(self.getWorld());
		final float maxHealth = self.getMaxHealth();

		if (this.shouldResetHealth || (enabled && self.getHealth() > maxHealth)) {
			this.setHealthData(maxHealth);
			this.shouldResetHealth = false;
		}
	}

}
