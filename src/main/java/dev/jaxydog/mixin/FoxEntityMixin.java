package dev.jaxydog.mixin;

import dev.jaxydog.utility.SprayableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity implements SprayableEntity, VariantHolder<FoxEntity.Type> {

	@Shadow
	@Final
	private static TrackedData<Optional<UUID>> OWNER;

	@Unique
	private @Nullable LivingEntity spraySource;

	@Unique
	private int sprayDuration = 0;

	protected FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract boolean isSitting();

	@Override
	public void astral$setSprayed(LivingEntity source, int ticks, boolean initialSpray) {
		if (source == null) return;

		this.spraySource = source;
		this.sprayDuration = Math.max(0, ticks);

		if (initialSpray && this.astral$isSprayed()) {
			this.playSound(SoundEvents.ENTITY_FOX_SCREECH, 2F, this.getSoundPitch());

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
		return !this.astral$isSprayed() && !(this.dataTracker.get(OWNER).isPresent() && this.isSitting());
	}

	@Inject(method = "initGoals", at = @At("HEAD"))
	private void initGoalsInject(CallbackInfo callbackInfo) {
		this.goalSelector.add(1, new EscapeSprayGoal<>(this, 1.5));
	}

}
