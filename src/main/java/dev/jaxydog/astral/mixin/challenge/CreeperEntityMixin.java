package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Implements the mob challenge system for creepers */
@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SkinOverlayOwner {

    /** The maximum allowed explosion power. */
    @Unique
    private static final double MAX_POWER = 50D;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    /** Modifies the creeper's explosion strength to account for mob challenge scaling */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "explode", at = @At("STORE"), ordinal = 0)
    private float powerVarInject(float power) {
        if (!MobChallengeUtil.shouldScale(this)) return power;

        final double additive = MobChallengeUtil.getAttackAdditive(this.getWorld());
        final double scaled = MobChallengeUtil.getScaledAdditive(this, additive);

        return (float) Math.min(power + (scaled / 10D), MAX_POWER);
    }

}
