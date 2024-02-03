package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.content.power.custom.ConditionedCooldownPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.entity.ModifyResourceAction;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModifyResourceAction.class)
public class ModifyResourceActionMixin {

    @Inject(
        method = "action", at = @At(
        value = "INVOKE_ASSIGN",
        target = "Lio/github/apace100/calio/data/SerializableData$Instance;get(Ljava/lang/String;)Ljava/lang/Object;",
        ordinal = 1,
        shift = Shift.AFTER
    ), cancellable = true
    )
    private static void actionConditionedCooldown(
        Instance data,
        Entity entity,
        CallbackInfo callbackInfo,
        @Local PowerHolderComponent component,
        @Local PowerType<?> powerType,
        @Local Power power
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            final Modifier modifier = data.get("modifier");
            int targetRemainingTicks = (int) modifier.apply(entity, cooldownPower.getRemainingTicks());

            if (targetRemainingTicks < 0) targetRemainingTicks = 0;

            cooldownPower.modify(targetRemainingTicks - cooldownPower.getRemainingTicks());

            PowerHolderComponent.syncPower(entity, powerType);

            callbackInfo.cancel();
        }
    }

}
