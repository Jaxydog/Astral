package dev.jaxydog.mixin.challenge;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Provides an accessor for private items within a living entity */
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

	/** Returns the living entity's health tracker */
	@Accessor("HEALTH")
	public static TrackedData<Float> getHealthTracker() {
		throw new AssertionError();
	}

}
