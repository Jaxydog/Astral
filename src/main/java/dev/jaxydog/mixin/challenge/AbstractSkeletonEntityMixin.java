package dev.jaxydog.mixin.challenge;

import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Implements the mob challenge system's attack changes for skeletons */
@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity implements RangedAttackMob {

	protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType,
			World world) {
		super(entityType, world);
	}

	/** Increases a skeleton's shot arrow's damage based on mob challenge scale */
	@Inject(method = "attack", at = @At("INVOKE"), cancellable = true)
	private void attackInject(LivingEntity target, float pullProgress, CallbackInfo callbackInfo) {
		if (!MobChallengeUtil.shouldScale(this)) {
			return;
		}

		final World world = this.getWorld();
		final Hand hand = ProjectileUtil.getHandPossiblyHolding(this, Items.BOW);
		final ItemStack stack = this.getProjectileType(this.getStackInHand(hand));
		final PersistentProjectileEntity projectile =
				ProjectileUtil.createArrowProjectile(this, stack, pullProgress);

		final double x = target.getX() - this.getX();
		final double y = target.getBodyY(1.0D / 3.0D) - projectile.getY();
		final double z = target.getZ() - this.getZ();
		final double h = Math.sqrt(x * x + z * z);

		final double additive = MobChallengeUtil.getAttackAdditive(world);
		final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

		projectile.setVelocity(x, y + h * 0.2D, z, 1.6f, 14 - world.getDifficulty().getId() * 4);
		projectile.setDamage(projectile.getDamage() + scaled);
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f,
				1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
		world.spawnEntity(projectile);

		callbackInfo.cancel();
	}

}
