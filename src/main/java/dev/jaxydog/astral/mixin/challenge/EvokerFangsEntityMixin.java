package dev.jaxydog.astral.mixin.challenge;

import dev.jaxydog.astral.utility.MobChallengeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EvokerFangsEntity.class)
public abstract class EvokerFangsEntityMixin extends Entity implements Ownable {

    public EvokerFangsEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyArg(
        method = "damage", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
    ), index = 1
    )
    private float damageArgsInject(float damage) {
        final Entity entity = this.getOwner() != null ? this.getOwner() : this;

        if (!MobChallengeUtil.shouldScale(entity)) return damage;

        final double additive = MobChallengeUtil.getAttackAdditive(entity.getWorld());

        return damage + (float) MobChallengeUtil.getScaledAdditive(entity, additive);
    }

}
