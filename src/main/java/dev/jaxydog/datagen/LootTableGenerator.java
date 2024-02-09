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

import dev.jaxydog.Astral;
import dev.jaxydog.utility.IdentifierMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.lang.reflect.Modifier.*;

/**
 * Provides a simple data generation API for advancements.
 *
 * @author Jaxydog
 */
public class LootTableGenerator extends SimpleFabricLootTableProvider {

    private static final Map<LootContextType, LootTableGenerator> instances = new Object2ObjectOpenHashMap<>();

    private final IdentifierMap<Builder> lootTables = new IdentifierMap<>();

    public LootTableGenerator(FabricDataOutput output, LootContextType type) {
        super(output, type);

        assert !instances.containsKey(type);

        instances.put(type, this);
    }

    public static void addAllProviders(Pack pack) {
        for (final Field field : LootContextTypes.class.getFields()) {
            final int modifiers = field.getModifiers();

            if (!(isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers))) continue;

            final LootContextType type;

            try {
                if (!(field.get(null) instanceof final LootContextType lootContextType)) continue;

                type = lootContextType;
            } catch (IllegalAccessException exception) {
                Astral.LOGGER.error(exception.getLocalizedMessage());

                continue;
            }

            pack.addProvider((Pack.Factory<? extends DataProvider>) output -> new LootTableGenerator(output, type));
        }
    }

    public static @NotNull LootTableGenerator getInstance(LootContextType type) {
        final LootTableGenerator instance = instances.get(type);

        assert instance != null;

        return instance;
    }

    public void generate(Identifier identifier, Builder builder) {
        this.lootTables.put(identifier, builder.type(this.lootContextType));
    }

    @Override
    public void accept(BiConsumer<Identifier, Builder> exporter) {
        this.lootTables.forEach(exporter);
    }

}
