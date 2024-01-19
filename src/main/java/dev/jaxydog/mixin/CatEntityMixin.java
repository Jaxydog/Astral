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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntity implements SprayableEntity, VariantHolder<CatVariant> {

	@Unique
	private @Nullable LivingEntity spraySource;

	@Unique
	private int sprayDuration = 0;

	protected CatEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract void hiss();

	@Override
	public void astral$setSprayed(@Nullable LivingEntity source, int ticks, boolean initialSpray) {
		this.spraySource = source;
		this.sprayDuration = Math.max(0, ticks);

		if (initialSpray && this.astral$isSprayed()) {
			this.hiss();

			if (this.isOnGround()) this.jump();
		}
	}

	@Override
	public @Nullable LivingEntity astral$getSpraySource() {
		return this.spraySource;
	}

	@Override
	public int astral$getSprayTicks() {
		return this.sprayDuration;
	}

	@Override
	public boolean astral$canSpray() {
		return !this.astral$isSprayed() && (!this.isTamed() || !this.isSitting());
	}

	@Inject(method = "initGoals", at = @At("HEAD"))
	private void initGoalsInject(CallbackInfo callbackInfo) {
		this.goalSelector.add(1, new EscapeSprayGoal<>(this, 1.5));
	}

}
