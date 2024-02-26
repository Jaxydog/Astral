package dev.jaxydog.astral.mixin;

import dev.jaxydog.astral.utility.injected.AstralLightningEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity implements AstralLightningEntity {

    @Unique
    private static final String PRESERVE_ITEMS_KEY = "PreserveItems";

    @Unique
    private boolean preservesItems = false;

    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean astral$preservesItems() {
        return this.preservesItems;
    }

    @Override
    public void astral$setPreservesItems(boolean preserve) {
        this.preservesItems = preserve;
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtInject(NbtCompound nbt, CallbackInfo callbackInfo) {
        if (nbt.contains(PRESERVE_ITEMS_KEY)) {
            this.preservesItems = nbt.getBoolean(PRESERVE_ITEMS_KEY);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtInject(NbtCompound nbt, CallbackInfo callbackInfo) {
        if (this.preservesItems) {
            nbt.putBoolean(PRESERVE_ITEMS_KEY, true);
        }
    }

}
