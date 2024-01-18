package dev.jaxydog.mixin;

import dev.jaxydog.utility.SprayableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity implements SprayableEntity, VariantHolder<FoxEntity.Type> {

	@Shadow
	public abstract boolean isSitting();

	@Unique
	private @Nullable LivingEntity spraySource;

	@Unique
	private int sprayDuration = 0;

	protected FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void astral$setSprayDuration(LivingEntity source, int ticks) {
		this.spraySource = source;
		this.sprayDuration = Math.max(0, ticks);

		this.jump();
		this.playSound(SoundEvents.ENTITY_FOX_SCREECH, 2F, this.getSoundPitch());
	}

	@Override
	public boolean astral$canSpray() {
		return SprayableEntity.super.astral$canSpray() && !this.isSitting();
	}

	@Override
	public @Nullable LivingEntity astral$getSpraySource() {
		return this.spraySource;
	}

	@Override
	public int astral$getSprayDuration() {
		return this.sprayDuration;
	}

	@Inject(method = "initGoals", at = @At("HEAD"))
	private void initGoalsInject(CallbackInfo callbackInfo) {
		this.goalSelector.add(1, new EscapeSprayGoal<>(this, 1.5));
	}

	@Inject(method = "tick", at = @At(value = "TAIL", shift = Shift.BEFORE))
	private void tickInject(CallbackInfo callbackInfo) {
		if (this.getWorld().isClient()) return;

		if (this.sprayDuration > 0) {
			this.sprayDuration -= 1;
		} else if (this.spraySource != null) {
			this.spraySource = null;
		}
	}

}
