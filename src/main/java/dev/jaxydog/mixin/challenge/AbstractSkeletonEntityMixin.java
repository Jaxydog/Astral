package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Implements the mob challenge system's attack changes for skeletons */
@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin {

	/** Returns the mixin's 'this' instance */
	private AbstractSkeletonEntity self() {
		return (AbstractSkeletonEntity) (Object) this;
	}

	/** Increases a skeleton's shot arrow's damage based on mob challenge scale */
	@Inject(method = "attack", at = @At("INVOKE"), cancellable = true)
	private void attackInject(LivingEntity target, float pullProgress, CallbackInfo callbackInfo) {
		if (((LivingEntityMixin) (Object) this).ignoreChallengeScaling) {
			return;
		}

		final AbstractSkeletonEntity self = this.self();

		if (!MobChallengeUtil.isEnabled(self.getWorld())) {
			return;
		}

		final Hand hand = ProjectileUtil.getHandPossiblyHolding(self, Items.BOW);
		final ItemStack stack = self.getProjectileType(self.getStackInHand(hand));
		final PersistentProjectileEntity projectile =
				ProjectileUtil.createArrowProjectile(self, stack, pullProgress);

		final double x = target.getX() - self.getX();
		final double y = target.getBodyY(1.0D / 3.0D) - projectile.getY();
		final double z = target.getZ() - self.getZ();
		final double h = Math.sqrt(x * x + z * z);

		final double additive = MobChallengeUtil.getAttackAdditive(self.getWorld());
		final double scaled = MobChallengeUtil.getScaledAdditive(self, additive);

		projectile.setVelocity(x, y + h * 0.2D, z, 1.6f,
				14 - self.getWorld().getDifficulty().getId() * 4);
		projectile.setDamage(projectile.getDamage() + scaled);
		self.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f,
				1.0f / (self.getRandom().nextFloat() * 0.4f + 0.8f));
		self.getWorld().spawnEntity(projectile);

		callbackInfo.cancel();
	}

}
