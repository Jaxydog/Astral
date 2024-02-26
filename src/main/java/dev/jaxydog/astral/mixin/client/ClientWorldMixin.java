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

package dev.jaxydog.mixin.client;

import dev.jaxydog.utility.EntityTrackingUnboundedSoundInstance;
import dev.jaxydog.utility.PositionedUnboundedSoundInstance;
import dev.jaxydog.utility.injected.AstralClientWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements AstralClientWorld {

    @Shadow
    @Final
    private MinecraftClient client;

    protected ClientWorldMixin(
        MutableWorldProperties properties,
        RegistryKey<World> registryRef,
        DynamicRegistryManager registryManager,
        RegistryEntry<DimensionType> dimensionEntry,
        Supplier<Profiler> profiler,
        boolean isClient,
        boolean debugWorld,
        long biomeAccess,
        int maxChainedNeighborUpdates
    ) {
        super(properties,
            registryRef,
            registryManager,
            dimensionEntry,
            profiler,
            isClient,
            debugWorld,
            biomeAccess,
            maxChainedNeighborUpdates
        );
    }

    @Override
    public void astral$playUnboundedSound(
        double x,
        double y,
        double z,
        SoundEvent event,
        SoundCategory category,
        float volume,
        float pitch,
        boolean useDistance,
        long seed
    ) {
        final double distance = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
        final SoundInstance instance = new PositionedUnboundedSoundInstance(event,
            category,
            volume,
            pitch,
            Random.create(seed),
            x,
            y,
            z
        );

        if (useDistance && distance > 100D) {
            this.client.getSoundManager().play(instance, (int) (Math.sqrt(distance) / 2D));
        } else {
            this.client.getSoundManager().play(instance);
        }
    }

    @Override
    public void astral$playUnboundedSoundFromEntity(
        @Nullable PlayerEntity except,
        Entity entity,
        SoundEvent event,
        SoundCategory category,
        float volume,
        float pitch,
        long seed
    ) {
        if (except != this.client.player) return;

        final SoundInstance instance = new EntityTrackingUnboundedSoundInstance(event,
            category,
            volume,
            pitch,
            entity,
            seed
        );

        this.client.getSoundManager().play(instance);
    }

}
