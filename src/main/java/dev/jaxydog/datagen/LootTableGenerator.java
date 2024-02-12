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

import dev.jaxydog.utility.IdentifierMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootTableGenerator implements DataProvider {

    private static @Nullable LootTableGenerator instance;

    private final Map<LootContextType, Instance> instances = new Object2ObjectOpenHashMap<>();
    private final Pack pack;

    public LootTableGenerator(Pack pack) {
        this.pack = pack;

        assert instance == null;

        instance = this;
    }

    public static @NotNull LootTableGenerator getInstance() {
        assert instance != null;

        return instance;
    }

    /**
     * Creates a new builder for the given loot table.
     *
     * @param identifier The loot table's identifier.
     * @param builder The builder.
     */
    public void generate(LootContextType lootContextType, Identifier identifier, Builder builder) {
        final Instance instance = this.instances.computeIfAbsent(lootContextType,
            type -> Instance.create(type, this.pack)
        );

        instance.generate(identifier, builder.type(lootContextType));
    }

    @Override
    public String getName() {
        return "Loot Tables";
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return CompletableFuture.allOf((CompletableFuture<?>[]) this.instances.values()
            .stream()
            .map(v -> v.run(writer))
            .toArray());
    }

    /**
     * An instance of a loot table generator.
     *
     * @author Jaxydog
     */
    private static class Instance extends SimpleFabricLootTableProvider {

        private final IdentifierMap<Builder> builders = new IdentifierMap<>();

        public Instance(FabricDataOutput output, LootContextType lootContextType) {
            super(output, lootContextType);
        }

        public static Instance create(LootContextType lootContextType, Pack pack) {
            return pack.addProvider((output, future) -> new Instance(output, lootContextType));
        }

        /**
         * Creates a new builder for the given loot table.
         *
         * @param identifier The loot table's identifier.
         * @param builder The builder.
         */
        public void generate(Identifier identifier, Builder builder) {
            this.builders.put(identifier, builder);
        }

        @Override
        public void accept(BiConsumer<Identifier, Builder> exporter) {
            this.builders.forEach(exporter);
        }

    }

}
