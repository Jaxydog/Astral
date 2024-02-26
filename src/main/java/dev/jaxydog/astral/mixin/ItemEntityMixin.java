package dev.jaxydog.mixin;

import dev.jaxydog.utility.injected.AstralLightningEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @SuppressWarnings("RedundantCast")
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damageInject(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (!(source.getAttacker() instanceof final LightningEntity lightning)) return;

        if (((AstralLightningEntity) lightning).astral$preservesItems()) {
            callbackInfo.setReturnValue(false);
        }
    }

}
