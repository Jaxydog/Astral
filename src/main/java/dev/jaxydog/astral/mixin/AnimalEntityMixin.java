package dev.jaxydog.mixin;

import dev.jaxydog.utility.CowType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

	@Unique
	private static final String COW_TYPE_KEY = "CowType";

	protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	private void readCowTypeInject(NbtCompound nbt, CallbackInfo callbackInfo) {
		final AnimalEntity self = (AnimalEntity) (Object) this;

		if (!(self instanceof CowEntity) || self instanceof MooshroomEntity) return;
		if (!this.getDataTracker().containsKey(CowType.COW_TYPE)) return;

		final String type;

		if (nbt.contains(COW_TYPE_KEY, NbtElement.STRING_TYPE)) {
			type = nbt.getString(COW_TYPE_KEY);
		} else {
			type = CowType.BROWN.asString();
		}

		this.getDataTracker().set(CowType.COW_TYPE, type);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	private void writeCowTypeInject(NbtCompound nbt, CallbackInfo callbackInfo) {
		final AnimalEntity self = (AnimalEntity) (Object) this;

		if (!(self instanceof CowEntity) || self instanceof MooshroomEntity) return;
		if (!this.getDataTracker().containsKey(CowType.COW_TYPE)) return;

		nbt.putString(COW_TYPE_KEY, this.getDataTracker().get(CowType.COW_TYPE));
	}

}
