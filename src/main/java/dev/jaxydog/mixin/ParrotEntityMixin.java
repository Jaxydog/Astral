package dev.jaxydog.mixin;

import dev.jaxydog.Astral;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Allows you to heal parrots with items */
@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin {

	@Unique
	private static final TagKey<Item> PARROT_FEED = TagKey.of(RegistryKeys.ITEM, Astral.getId("parrot_feed"));

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void interactMobInject(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo) {
		final ItemStack stack = player.getStackInHand(hand);
		final ParrotEntity self = (ParrotEntity) (Object) this;
		final World world = player.getWorld();
		final boolean missingHealth = self.getHealth() < self.getMaxHealth();
		final boolean canFeed = stack.isIn(PARROT_FEED);

		if (world.isClient || !self.isTamed() || !canFeed || !missingHealth) return;

		if (!self.isSilent()) {
			final float pitchModifier = self.getRandom().nextFloat() - self.getRandom().nextFloat();

			self.playSound(SoundEvents.ENTITY_PARROT_EAT, 1F, 1F + (pitchModifier * 0.2F));
		}

		if (!player.getAbilities().creativeMode) stack.decrement(1);

		self.getWorld().sendEntityStatus(self, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
		self.heal(1);

		callbackInfo.setReturnValue(ActionResult.SUCCESS);
	}

}
