package dev.jaxydog.mixin.challenge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin extends AnimalEntity implements Angerable, Flutterer {

	protected BeeEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Invoker("setHasStung")
	public abstract void setHasStungInvoker(boolean hasStung);

	@Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
	private void tryAttackInject(Entity target, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!MobChallengeUtil.shouldScale(this)) {
			return;
		}

		final double base = this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

		final DamageSource source = this.getDamageSources().sting(this);
		final boolean applyEffects = target.damage(source, (float) (base + scaled));

		if (applyEffects) {
			this.applyDamageEffects(this, target);

			if (target instanceof LivingEntity living) {
				living.setStingerCount(living.getStingerCount() + 1);

				int duration = 0;

				if (this.getWorld().getDifficulty() == Difficulty.NORMAL) {
					duration = 10;
				} else if (this.getWorld().getDifficulty() == Difficulty.HARD) {
					duration = 18;
				}

				if (duration > 0) {
					final StatusEffectInstance status =
							new StatusEffectInstance(StatusEffects.POISON, duration * 20, 0);

					living.addStatusEffect(status, this);
				}
			}

			this.setHasStungInvoker(true);
			this.stopAnger();
			this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0f, 1.0f);
		}

		callbackInfo.setReturnValue(applyEffects);
	}

}
