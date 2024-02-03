package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.content.power.custom.ConditionedCooldownPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.condition.entity.ResourceCondition;
import io.github.apace100.apoli.util.Comparison;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceCondition.class)
public class ResourceConditionMixin {

    @Inject(method = "comparePowerValue", at = @At(value = "RETURN", shift = Shift.BEFORE), cancellable = true)
    private static void comparePowerValueConditionedCooldown(
        PowerHolderComponent component,
        PowerType<?> powerType,
        Comparison comparison,
        int compareTo,
        CallbackInfoReturnable<Boolean> callbackInfo,
        @Local Integer powerValue,
        @Local Power power
    ) {
        if (powerValue != null || !(power instanceof final ConditionedCooldownPower cooldownPower)) return;

        callbackInfo.setReturnValue(comparison.compare(cooldownPower.getRemainingTicks(), compareTo));
    }

}
