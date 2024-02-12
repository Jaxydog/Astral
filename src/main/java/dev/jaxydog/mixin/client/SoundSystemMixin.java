package dev.jaxydog.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @ModifyReturnValue(method = "getAdjustedPitch", at = @At("RETURN"))
    private float unAdjustPitch(float original, @Local(argsOnly = true) SoundInstance sound) {
        return MathHelper.clamp(sound.getPitch(), 0.01F, 255F);
    }

}
