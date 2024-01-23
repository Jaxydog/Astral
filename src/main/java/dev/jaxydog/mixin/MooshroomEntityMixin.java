package dev.jaxydog.mixin;

import dev.jaxydog.content.item.CustomItems;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.CowEntity;
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
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MooshroomEntity.class)
public abstract class MooshroomEntityMixin extends PassiveEntity implements Shearable, VariantHolder<MooshroomEntity.Type> {

	@Shadow
	private @Nullable StatusEffect stewEffect;

	@Shadow
	private int stewEffectDuration;

	public MooshroomEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void milking(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo){
		final ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(Items.GLASS_BOTTLE) && !this.isBaby()) {
			boolean bl = false;
			ItemStack itemStack2;
			if (this.stewEffect != null) {
				bl = true;
				itemStack2 = new ItemStack(CustomItems.CHOCOLATE_MILK);
				SuspiciousStewItem.addEffectToStew(itemStack2, this.stewEffect, this.stewEffectDuration);
				this.stewEffect = null;
				this.stewEffectDuration = 0;
			} else {
				itemStack2 = new ItemStack(CustomItems.CHOCOLATE_MILK);
			}

			ItemStack itemStack3 = ItemUsage.exchangeStack(stack, player, itemStack2, false);
			player.setStackInHand(hand, itemStack3);
			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
			} else {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_MILK;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			callbackInfo.setReturnValue(ActionResult.success(player.getWorld().isClient()));
		}
	}
}
