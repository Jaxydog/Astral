package dev.jaxydog.mixin;

import dev.jaxydog.content.item.CustomItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MooshroomEntity.class)
public abstract class MooshroomEntityMixin extends PassiveEntity
	implements Shearable, VariantHolder<MooshroomEntity.Type> {

	@Shadow
	private @Nullable StatusEffect stewEffect;

	@Shadow
	private int stewEffectDuration;

	public MooshroomEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void milking(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo) {
		final ItemStack stack = player.getStackInHand(hand);

		if (!stack.isOf(Items.GLASS_BOTTLE) || this.isBaby()) return;

		final ItemStack result = CustomItems.CHOCOLATE_MILK.getDefaultStack();
		final SoundEvent soundEvent;

		if (this.stewEffect != null) {
			SuspiciousStewItem.addEffectToStew(result, this.stewEffect, this.stewEffectDuration);

			this.stewEffect = null;
			this.stewEffectDuration = 0;

			soundEvent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
		} else {
			soundEvent = SoundEvents.ENTITY_MOOSHROOM_MILK;
		}

		final ItemStack exchanged = ItemUsage.exchangeStack(stack, player, result, false);

		player.setStackInHand(hand, exchanged);

		this.playSound(soundEvent, 1F, 1F);

		callbackInfo.setReturnValue(ActionResult.success(player.getWorld().isClient()));
	}

}
