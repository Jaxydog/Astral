package dev.jaxydog.mixin;

import net.minecraft.server.command.PlaySoundCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlaySoundCommand.class)
public class PlaySoundCommandMixin {

    @ModifyArg(
        method = "makeArgumentsForCategory", at = @At(
        value = "INVOKE",
        target = "Lcom/mojang/brigadier/arguments/FloatArgumentType;floatArg(FF)Lcom/mojang/brigadier/arguments/FloatArgumentType;",
        ordinal = 0
    ), index = 1
    )
    private static float changeMaxPitch(float min) {
        return 255F;
    }

}
