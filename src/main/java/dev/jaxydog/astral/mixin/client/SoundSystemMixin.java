package dev.jaxydog.astral.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.astral.utility.EntityTrackingUnboundedSoundInstance;
import dev.jaxydog.astral.utility.PositionedUnboundedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {

    @Shadow
    protected abstract float getSoundVolume(@Nullable SoundCategory category);

    @WrapOperation(
        method = "method_19754(Lnet/minecraft/client/sound/SoundInstance;Lnet/minecraft/client/sound/Channel$SourceManager;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F"
        )
    )
    private float updateUnboundedVolume(
        SoundSystem self, SoundInstance instance, Operation<Float> original
    ) {
        if (instance instanceof PositionedUnboundedSoundInstance) {
            return Math.max(0F, instance.getVolume() * this.getSoundVolume(instance.getCategory()));
        } else {
            return original.call(self, instance);
        }
    }

    @WrapOperation(
        method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F"
    )
    )
    private float playUnboundedVolume(
        SoundSystem self,
        float volume,
        SoundCategory category,
        Operation<Float> original,
        @Local(argsOnly = true) SoundInstance instance
    ) {
        if (instance instanceof PositionedUnboundedSoundInstance) {
            return Math.max(0F, volume * this.getSoundVolume(category));
        } else {
            return original.call(self, volume, category);
        }
    }

    @WrapOperation(
        method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedPitch(Lnet/minecraft/client/sound/SoundInstance;)F"
    )
    )
    private float playUnboundedPitch(SoundSystem self, SoundInstance instance, Operation<Float> original) {
        if (instance instanceof PositionedUnboundedSoundInstance) {
            // Not true zero, but there's no check for a pitch of `0` and my worry is that it will produce a sound that never finishes playing.
            return Math.max(0.001F, instance.getPitch());
        } else {
            return original.call(self, instance);
        }
    }

    @WrapOperation(
        method = "tick()V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F"
    )
    )
    private float tickUnboundedVolume(SoundSystem self, SoundInstance instance, Operation<Float> original) {
        if (instance instanceof EntityTrackingUnboundedSoundInstance) {
            return Math.max(0F, instance.getVolume() * this.getSoundVolume(instance.getCategory()));
        } else {
            return original.call(self, instance);
        }
    }

    @WrapOperation(
        method = "tick()V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedPitch(Lnet/minecraft/client/sound/SoundInstance;)F"
    )
    )
    private float tickUnboundedPitch(SoundSystem self, SoundInstance instance, Operation<Float> original) {
        if (instance instanceof EntityTrackingUnboundedSoundInstance) {
            // Not true zero, but there's no check for a pitch of `0` and my worry is that it will produce a sound that never finishes playing.
            return Math.max(0.001F, instance.getPitch());
        } else {
            return original.call(self, instance);
        }
    }

}
