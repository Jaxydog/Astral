package dev.jaxydog.mixin.challenge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.jaxydog.utility.MobChallengeUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Implements the mob challenge system for slimes */
@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntity implements Monster {

	protected SlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyReturnValue(method = "getDamageAmount", at = @At("RETURN"))
	private float getDamageAmountMixin(float damage) {
		if (!MobChallengeUtil.shouldScale(this)) return damage;

		final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());

		return damage + (float) MobChallengeUtil.getScaledAdditive(this, additive);
	}

}
