/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.astral.utility.PlayUnboundedSoundFromEntityS2CPacket;
import dev.jaxydog.astral.utility.PlayUnboundedSoundS2CPacket;
import dev.jaxydog.astral.utility.injected.AstralClientWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayNetworkHandler.class, priority = 0)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener, TickablePacketListener {

    @Shadow
    @Final
    private MinecraftClient client;

    @SuppressWarnings("RedundantCast")
    @WrapOperation(
        method = "onPlaySound", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
    )
    )
    private void unbindSound(
        ClientWorld self,
        PlayerEntity except,
        double x,
        double y,
        double z,
        RegistryEntry<SoundEvent> sound,
        SoundCategory category,
        float volume,
        float pitch,
        long seed,
        Operation<Void> original,
        @Local(argsOnly = true) PlaySoundS2CPacket packet
    ) {
        if (this.client.world != null && packet instanceof PlayUnboundedSoundS2CPacket) {
            ((AstralClientWorld) this.client.world).astral$playUnboundedSound(x,
                y,
                z,
                sound.value(),
                category,
                volume,
                pitch,
                false,
                seed
            );
        } else {
            original.call(self, except, x, y, z, sound, category, volume, pitch, seed);
        }
    }

    @SuppressWarnings("RedundantCast")
    @WrapOperation(
        method = "onPlaySoundFromEntity", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/world/ClientWorld;playSoundFromEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
    )
    )
    private void unbindSoundFromEntity(
        ClientWorld self,
        PlayerEntity except,
        Entity entity,
        RegistryEntry<SoundEvent> sound,
        SoundCategory category,
        float volume,
        float pitch,
        long seed,
        Operation<Void> original,
        @Local(argsOnly = true) PlaySoundFromEntityS2CPacket packet
    ) {
        if (this.client.world != null && packet instanceof PlayUnboundedSoundFromEntityS2CPacket) {
            ((AstralClientWorld) this.client.world).astral$playUnboundedSoundFromEntity(except,
                entity,
                sound.value(),
                category,
                volume,
                pitch,
                seed
            );
        } else {
            original.call(self, except, entity, sound, category, volume, pitch, seed);
        }
    }

}
