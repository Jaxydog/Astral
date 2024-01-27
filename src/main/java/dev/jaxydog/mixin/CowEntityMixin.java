package dev.jaxydog.mixin;

import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.utility.CowType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
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

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends PassiveEntityMixin {

	@Shadow
	public abstract @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity);

	protected CowEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void cowOverride(CowEntity cow) {
		if (this.getRandom().nextInt(124) == 0) {
			this.dataTracker.set(CowType.COW_TYPE, CowType.PINK.asString());
		}
	}

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void milking(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo) {
		ItemStack stack = player.getStackInHand(hand);

		if (stack.isOf(Items.GLASS_BOTTLE) && !this.isBaby() && this.getDataTracker()
			.get(CowType.COW_TYPE)
			.equals(CowType.PINK.asString())) {
			player.playSound(SoundEvents.ENTITY_COW_MILK, 1F, 1F);

			final ItemStack result = CustomItems.STRAWBERRY_MILK.getDefaultStack();
			final ItemStack exchanged = ItemUsage.exchangeStack(stack, player, result, false);

			player.setStackInHand(hand, exchanged);

			callbackInfo.setReturnValue((ActionResult.success(player.getWorld().isClient())));
		}

	}

	@Inject(
		method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/CowEntity;",
		at = @At("HEAD"),
		cancellable = true
	)
	// im so sorry jaxy, I tried my best... good luck
	private void genetics(
		ServerWorld serverWorld, PassiveEntity passiveEntity, CallbackInfoReturnable<CowEntity> callbackInfo
	) {
		CowEntity cow = EntityType.COW.create(serverWorld);
		String type = this.getDataTracker().get(CowType.COW_TYPE);
		String type2 = passiveEntity.getDataTracker().get(CowType.COW_TYPE);
		if (cow == null) {
			callbackInfo.cancel();
		}
		if (type.equals(CowType.PINK.asString()) && type2.equals(CowType.PINK.asString())) {
			if (getRandom().nextInt(1) == 0) {
				cow.getDataTracker().set(CowType.COW_TYPE, CowType.PINK.asString());
				callbackInfo.setReturnValue(cow);
			} else {
				cow.getDataTracker().set(CowType.COW_TYPE, CowType.BROWN.asString());
				callbackInfo.setReturnValue(cow);
			}
		}
		if ((type.equals(CowType.PINK.asString()) && type2.equals(CowType.BROWN.asString()))
			|| (type.equals(CowType.BROWN.asString()) && type2.equals(CowType.PINK.asString()))) {
			if (getRandom().nextInt(2) == 0) {
				cow.getDataTracker().set(CowType.COW_TYPE, CowType.PINK.asString());
				callbackInfo.setReturnValue(cow);
			} else {
				cow.getDataTracker().set(CowType.COW_TYPE, CowType.BROWN.asString());
				callbackInfo.setReturnValue(cow);
			}
		}
		if (type.equals(CowType.BROWN.asString()) && type2.equals(CowType.BROWN.asString())) {
			if (getRandom().nextInt(124) == 0) {
				cow.getDataTracker().set(CowType.COW_TYPE, CowType.PINK.asString());
				callbackInfo.setReturnValue(cow);
			} else {
				cow.getDataTracker().set(CowType.COW_TYPE, CowType.BROWN.asString());
				callbackInfo.setReturnValue(cow);

			}
		}
	}

}
