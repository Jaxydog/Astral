package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.ChallengeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Implements the mob challenge system's attack changes for skeletons */
@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {

	/** Increases a skeleton's shot arrow's damage based on mob challenge scale */
	@Inject(method = "attack", at = @At("INVOKE"), cancellable = true)
	private void attackInject(LivingEntity target, float pullProgress, CallbackInfo callbackInfo) {
		var self = (AbstractSkeletonEntity) (Object) this;

		if (!ChallengeUtil.isEnabled(self.getWorld())) return;

		var hand = ProjectileUtil.getHandPossiblyHolding(self, Items.BOW);
		var stack = self.getProjectileType(self.getStackInHand(hand));
		var projectile = ProjectileUtil.createArrowProjectile(self, stack, pullProgress);

		var x = target.getX() - self.getX();
		var y = target.getBodyY(1.0D / 3.0D) - projectile.getY();
		var z = target.getZ() - self.getZ();
		var h = Math.sqrt(x * x + z * z);

		var additive = ChallengeUtil.getAttackAdditive(self.getWorld());
		var modifier = ChallengeUtil.getChallengeModifier(self, additive);

		projectile.setVelocity(x, y + h * 0.2D, z, 1.6f, 14 - self.world.getDifficulty().getId() * 4);
		projectile.setDamage(projectile.getDamage() + modifier);
		self.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (self.getRandom().nextFloat() * 0.4f + 0.8f));
		self.getWorld().spawnEntity(projectile);

		callbackInfo.cancel();
	}
}
