package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.server.command.PlaySoundCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlaySoundCommand.class)
public class PlaySoundCommandMixin {

    @ModifyExpressionValue(
        method = "makeArgumentsForCategory", at = @At(
        value = "INVOKE",
        target = "Lcom/mojang/brigadier/arguments/FloatArgumentType;floatArg(FF)Lcom/mojang/brigadier/arguments/FloatArgumentType;",
        ordinal = 0
    )
    )
    private static FloatArgumentType changePitchLimits(FloatArgumentType original) {
        return FloatArgumentType.floatArg(0F, 255F);
    }

}
