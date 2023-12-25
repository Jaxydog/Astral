package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PufferfishEntity.class)
public abstract class PufferfishEntityMixin extends FishEntity {

	public PufferfishEntityMixin(EntityType<? extends FishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "sting", at = @At("HEAD"), cancellable = true)
	private void stingInject(MobEntity mob, CallbackInfo callbackInfo) {
		if (!MobChallengeUtil.shouldScale(this)) return;

		final int puff = this.getPuffState();
		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);
		final double damage = 1D + (double) puff + scaled;

		if (mob.damage(this.getDamageSources().mobAttack(this), (float) damage)) {
			mob.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * puff, 0), this);
			this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1F, 1F);
		}

		callbackInfo.cancel();
	}

	@Shadow
	public abstract int getPuffState();

	@Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
	private void onPlayerCollisionInject(PlayerEntity player, CallbackInfo callbackInfo) {
		if (!MobChallengeUtil.shouldScale(this)) return;

		final int puff = this.getPuffState();
		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);
		final double damage = 1D + (double) puff + scaled;

		final DamageSource source = this.getDamageSources().mobAttack(this);
		final boolean applyEffects = player.damage(source, (float) damage);

		if (player instanceof final ServerPlayerEntity server && puff > 0 && applyEffects) {
			final StatusEffectInstance status = new StatusEffectInstance(StatusEffects.POISON, 60 * puff, 0);

			if (!this.isSilent()) {
				server.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PUFFERFISH_STING,
					GameStateChangeS2CPacket.DEMO_OPEN_SCREEN
				));
			}

			player.addStatusEffect(status, this);
		}

		callbackInfo.cancel();
	}

}
