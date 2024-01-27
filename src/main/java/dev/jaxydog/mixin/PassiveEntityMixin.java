package dev.jaxydog.mixin;

import dev.jaxydog.utility.CowType;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity {

	protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	protected void cowOverride(CowEntity cow) {
	}

	@Inject(method = "initialize", at = @At("TAIL"))
	private void pain(
		ServerWorldAccess world,
		LocalDifficulty difficulty,
		SpawnReason spawnReason,
		EntityData entityData,
		NbtCompound entityNbt,
		CallbackInfoReturnable<EntityData> callbackInfo
	) {
		if ((PassiveEntity) (Object) this instanceof final CowEntity cow) {
			this.cowOverride(cow);
		}
	}

	@Inject(method = "initDataTracker", at = @At("TAIL"))
	private void trackCowType(CallbackInfo callbackInfo) {
		if ((PassiveEntity) (Object) this instanceof CowEntity) {
			this.dataTracker.startTracking(CowType.COW_TYPE, CowType.BROWN.asString());
		}
	}

}
