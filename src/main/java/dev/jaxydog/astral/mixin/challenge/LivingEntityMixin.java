package dev.jaxydog.astral.mixin.challenge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.jaxydog.astral.utility.MobChallengeUtil;
import dev.jaxydog.astral.utility.injected.AstralLivingEntity;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Implements the mob challenge system's health changes */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, AstralLivingEntity {

    @Shadow
    @Final
    private static TrackedData<Float> HEALTH;

    /** If true, the mob will *always* scale. */
    @Unique
    private boolean forceChallengeScaling = false;

    /** Stores whether this entity ignores challenge scaling rules. */
    @Unique
    private boolean ignoreChallengeScaling = false;
    /**
     * Stores whether the entity needs to reset its health; should only be true if the gamerules are updated or when the
     * entity is first spawned
     */
    @Unique
    private boolean shouldResetHealth = true;
    /**
     * Stores whether mob challenge was previously enabled to determine whether the entity's health should be updated
     */
    @Unique
    private boolean lastEnableState = MobChallengeUtil.isEnabled(this.self().getWorld());
    /**
     * Stores the previously used health additive value to check whether the entity's health should be updated
     */
    @Unique
    private double lastHealthAdditive = MobChallengeUtil.getHealthAdditive(this.self().getWorld());
    /**
     * Stores the previously used chunk step value to check whether the entity's health should be updated
     */
    @Unique
    private double lastChunkStep = MobChallengeUtil.getChunkStep(this.self().getWorld());

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean astral$ignoresChallengeScaling() {
        return this.ignoreChallengeScaling;
    }

    @Override
    public boolean astral$forcesChallengeScaling() {
        return this.forceChallengeScaling;
    }

    /** Provides a scaled maximum health value if mob challenge scaling is enabled */
    @ModifyReturnValue(method = "getMaxHealth", at = @At("RETURN"))
    private float scaleHealth(float health) {
        if (!MobChallengeUtil.shouldScale(this) || this.getWorld().isClient()) return health;

        final World world = this.getWorld();
        final double additive = MobChallengeUtil.getHealthAdditive(world);

        if (this.lastHealthAdditive != additive) {
            this.lastHealthAdditive = additive;
            this.shouldResetHealth = true;
        }

        final int chunkStep = MobChallengeUtil.getChunkStep(world);

        if (this.lastChunkStep != chunkStep) {
            this.shouldResetHealth = true;
            this.lastChunkStep = chunkStep;
        }

        return health + (float) MobChallengeUtil.getScaledAdditive(this, additive);
    }

    /** Returns the mixin's 'this' instance */
    @Unique
    private LivingEntity self() {
        return (LivingEntity) (Object) this;
    }

    /** Automatically updates an entity's maximum health if necessary */
    @Inject(method = "tick", at = @At("TAIL"))
    private void tickInject(CallbackInfo callbackInfo) {
        if (!MobChallengeUtil.shouldScale(this) || this.getWorld().isClient()) return;

        final boolean enabled = MobChallengeUtil.isEnabled(this.getWorld());
        final float maxHealth = this.self().getMaxHealth();

        if (this.lastEnableState != enabled) {
            this.lastEnableState = enabled;
            this.shouldResetHealth = true;
        }

        if (this.shouldResetHealth) {
            this.setHealthData(maxHealth);
            this.shouldResetHealth = false;
        }
    }

    /**
     * Convenience method to set the entity's current health to the given value without calling `getMaxHealth()`
     */
    @Unique
    private void setHealthData(float health) {
        this.getDataTracker().set(HEALTH, Math.max(0, health));
    }

    /** Deserializes the `ignoreChallengeScaling` field. */
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtInject(NbtCompound nbt, CallbackInfo callbackInfo) {
        if (nbt.contains(MobChallengeUtil.IGNORE_KEY, NbtElement.BYTE_TYPE)) {
            this.ignoreChallengeScaling = nbt.getBoolean(MobChallengeUtil.IGNORE_KEY);
        }
        if (nbt.contains(MobChallengeUtil.FORCE_KEY, NbtElement.BYTE_TYPE)) {
            this.forceChallengeScaling = nbt.getBoolean(MobChallengeUtil.IGNORE_KEY);
        }
    }

    /** Serializes the `ignoreChallengeScaling` field. */
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtInject(NbtCompound nbt, CallbackInfo callbackInfo) {
        if (this.ignoreChallengeScaling) {
            nbt.putBoolean(MobChallengeUtil.IGNORE_KEY, true);
        }
        if (this.forceChallengeScaling) {
            nbt.putBoolean(MobChallengeUtil.IGNORE_KEY, true);
        }
    }

}
