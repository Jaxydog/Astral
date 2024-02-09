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

package dev.jaxydog.datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Provides a simple data generation API for tags.
 *
 * @author Jaxydog
 */
public class TagGenerator implements DataProvider {

    private static @Nullable TagGenerator instance;

    private final Map<RegistryKey<? extends Registry<?>>, Instance<?>> instances = new Object2ObjectOpenHashMap<>();
    private final Pack pack;

    public TagGenerator(Pack pack) {
        this.pack = pack;

        assert instance == null;

        instance = this;
    }

    public static @NotNull TagGenerator getInstance() {
        assert instance != null;

        return instance;
    }

    /**
     * Creates a new builder for the given tag.
     *
     * @param tagKey The requested tag.
     * @param builder The builder consumer.
     */
    @SuppressWarnings("unchecked")
    public <T> void generate(TagKey<T> tagKey, Consumer<FabricTagProvider<T>.FabricTagBuilder> builder) {
        final Instance<T> instance = (Instance<T>) this.instances.computeIfAbsent(tagKey.registry(), (key) -> {
            final RegistryKey<? extends Registry<T>> registryKey = (RegistryKey<? extends Registry<T>>) key;

            return Instance.create(registryKey, this.pack);
        });

        instance.generate(tagKey, builder);
    }

    @Override
    public String getName() {
        return "Tags";
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return CompletableFuture.allOf((CompletableFuture<?>[]) this.instances.values()
            .stream()
            .map(v -> v.run(writer))
            .toArray());
    }

    /**
     * An instance of a tag generator.
     *
     * @param <T> The associated registry type.
     *
     * @author Jaxydog
     */
    private static class Instance<T> extends FabricTagProvider<T> {

        private final Map<TagKey<T>, Consumer<FabricTagBuilder>> builders = new Object2ObjectOpenHashMap<>();

        private Instance(
            FabricDataOutput output,
            RegistryKey<? extends Registry<T>> registryKey,
            CompletableFuture<WrapperLookup> future
        ) {
            super(output, registryKey, future);
        }

        public static <T> Instance<T> create(RegistryKey<? extends Registry<T>> key, Pack pack) {
            return pack.addProvider((output, future) -> new Instance<>(output, key, future));
        }

        /**
         * Creates a new builder for the given tag.
         *
         * @param tagKey The requested tag.
         * @param builder The builder consumer.
         */
        public void generate(TagKey<T> tagKey, Consumer<FabricTagBuilder> builder) {
            this.builders.put(tagKey, builder);
        }

        @Override
        public void configure(WrapperLookup lookup) {
            this.builders.forEach((key, builder) -> builder.accept(this.getOrCreateTagBuilder(key)));
        }

    }

}
