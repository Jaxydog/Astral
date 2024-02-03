package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.content.power.custom.ConditionedCooldownPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.EntityActions;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityActions.class)
public class EntityActionsMixin {

    @Inject(
        method = "lambda$register$15(Lio/github/apace100/calio/data/SerializableData$Instance;Lnet/minecraft/entity/Entity;)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lio/github/apace100/calio/data/SerializableData$Instance;getInt(Ljava/lang/String;)I",
            shift = Shift.AFTER
        ),
        cancellable = true
    )
    private static void changeResourceConditionedCooldown(
        Instance data,
        Entity entity,
        CallbackInfo callbackInfo,
        @Local PowerHolderComponent component,
        @Local PowerType<?> powerType,
        @Local Power power,
        @Local ResourceOperation operation,
        @Local int changeInTicks
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            if (operation == ResourceOperation.ADD) {
                cooldownPower.modify(changeInTicks);
            } else {
                cooldownPower.setCooldown(changeInTicks);
            }

            PowerHolderComponent.syncPower(entity, powerType);

            callbackInfo.cancel();
        }
    }

    @Inject(
        method = "lambda$register$21(Lio/github/apace100/calio/data/SerializableData$Instance;Lnet/minecraft/entity/Entity;)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lio/github/apace100/apoli/component/PowerHolderComponent;getPower(Lio/github/apace100/apoli/power/PowerType;)Lio/github/apace100/apoli/power/Power;",
            shift = Shift.AFTER
        ),
        cancellable = true
    )
    private static void triggerCooldownConditionedCooldown(
        Instance data,
        Entity entity,
        CallbackInfo callbackInfo,
        @Local PowerHolderComponent component,
        @Local Power power
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.use();
            callbackInfo.cancel();
        }
    }

    @Inject(
        method = "lambda$register$24(Lio/github/apace100/calio/data/SerializableData$Instance;Lnet/minecraft/entity/Entity;)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lio/github/apace100/calio/data/SerializableData$Instance;getInt(Ljava/lang/String;)I",
            shift = Shift.AFTER
        ),
        cancellable = true
    )
    private static void setResourceConditionedCooldown(
        Instance data,
        Entity entity,
        CallbackInfo callbackInfo,
        @Local PowerHolderComponent component,
        @Local PowerType<?> powerType,
        @Local Power power,
        @Local int ticks
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.setCooldown(ticks);

            PowerHolderComponent.syncPower(entity, powerType);

            callbackInfo.cancel();
        }
    }

}
