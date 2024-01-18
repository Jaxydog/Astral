package dev.jaxydog.mixin;

import dev.jaxydog.utility.SprayableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntity implements SprayableEntity, VariantHolder<CatVariant> {

	@Unique
	private @Nullable LivingEntity spraySource;

	@Unique
	private int sprayDuration = 0;

	protected CatEntityMixin(
		EntityType<? extends TameableEntity> entityType, World world
	) {
		super(entityType, world);
	}

	@Override
	public void astral$setSprayDuration(LivingEntity source, int ticks) {
		this.spraySource = source;
		this.sprayDuration = Math.max(0, ticks);
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
