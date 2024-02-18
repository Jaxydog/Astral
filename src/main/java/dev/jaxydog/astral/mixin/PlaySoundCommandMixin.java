package dev.jaxydog.astral.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.jaxydog.astral.utility.PlayUnboundedSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.PlaySoundCommand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = PlaySoundCommand.class, priority = 0)
public class PlaySoundCommandMixin {

    @ModifyArg(
        method = "makeArgumentsForCategory", at = @At(
        value = "INVOKE",
        target = "Lcom/mojang/brigadier/arguments/FloatArgumentType;floatArg(FF)Lcom/mojang/brigadier/arguments/FloatArgumentType;",
        ordinal = 0
    ), index = 1
    )
    private static float changeMaxPitch(float min) {
        return Float.MAX_VALUE;
    }

    @WrapOperation(
        method = "execute", at = @At(
        value = "NEW",
        target = "(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;DDDFFJ)Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;"
    )
    )
    private static PlaySoundS2CPacket replacePacket(
        RegistryEntry<SoundEvent> sound,
        SoundCategory category,
        double x,
        double y,
        double z,
        float volume,
        float pitch,
        long seed,
        Operation<PlaySoundS2CPacket> original
    ) {
        return new PlayUnboundedSoundS2CPacket(sound, category, x, y, z, volume, pitch, seed);
    }

}
