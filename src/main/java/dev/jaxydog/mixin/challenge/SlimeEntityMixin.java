package dev.jaxydog.mixin.challenge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;

/** Implements the mob challenge system for slimes */
@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntity implements Monster {

	protected SlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "getDamageAmount", at = @At("RETURN"), cancellable = true)
	private void getDamageAmountMixin(CallbackInfoReturnable<Float> callbackInfo) {
		if (!MobChallengeUtil.shouldScale(this)) {
			return;
		}

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

		callbackInfo.setReturnValue(callbackInfo.getReturnValue() + (float) scaled);
	}

}
