package dev.jaxydog.mixin;

import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.common.collect.Sets;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/** Allows you to heal parrots with items */
@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin {

	private static final Set<Item> SEEDS = Sets.newHashSet(
		Items.WHEAT_SEEDS,
		Items.MELON_SEEDS,
		Items.PUMPKIN_SEEDS,
		Items.BEETROOT_SEEDS,
		Items.TORCHFLOWER_SEEDS,
		Items.PITCHER_POD);

	/** Returns the mixin's 'this' instance */
	private ParrotEntity self() {
		return (ParrotEntity) (Object) this;
	}

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void interactMobInject(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo) {
		final ItemStack stack = player.getStackInHand(hand);
		final ParrotEntity self = this.self();
		final World world = player.getWorld();
		final boolean missingHealth = self.getHealth() < self.getMaxHealth();
		final boolean canFeed = SEEDS.contains(stack.getItem());

		if (world.isClient || !self.isTamed() || !canFeed || !missingHealth) {
			return;
		}

		if (!self.isSilent()) {
			final float pitchModifier = self.getRandom().nextFloat() - self.getRandom().nextFloat();

			self.getWorld().playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_PARROT_EAT,
				self.getSoundCategory(), 1.0f, 1.0f + pitchModifier * 0.2f);
		}
		if (!player.getAbilities().creativeMode) {
			stack.decrement(1);
		}

		self.getWorld().sendEntityStatus(self, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
		self.heal(1);
		callbackInfo.setReturnValue(ActionResult.SUCCESS);
	}

}
