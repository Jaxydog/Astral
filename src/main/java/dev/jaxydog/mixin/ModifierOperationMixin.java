package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.content.power.custom.ConditionedCooldownPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.modifier.IModifierOperation;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierOperation;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ModifierOperation.class)
public abstract class ModifierOperationMixin implements IModifierOperation {

    @SuppressWarnings({ "UnresolvedMixinReference", "LocalMayBeArgsOnly" })
    @Inject(
        method = "lambda$apply$13", at = @At(
        value = "INVOKE_ASSIGN",
        target = "Lio/github/apace100/apoli/component/PowerHolderComponent;getPower(Lio/github/apace100/apoli/power/PowerType;)Lio/github/apace100/apoli/power/Power;",
        shift = Shift.AFTER
    ), cancellable = true
    )
    private static void applyConditionedCooldown(
        Entity entity,
        Instance instance,
        CallbackInfoReturnable<Double> callbackInfo,
        @Local PowerHolderComponent component,
        @Local PowerType<?> powerType,
        @Local Power power
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            double value = cooldownPower.getRemainingTicks();

            if (instance.isPresent("modifier")) {
                final List<Modifier> modifiers = instance.get("modifier");

                value = ModifierUtil.applyModifiers(entity, modifiers, value);
            }

            callbackInfo.setReturnValue(value);
        }
    }

}
