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

package dev.jaxydog.astral.datagen;

import dev.jaxydog.astral.utility.IdentifierMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.Advancement.Builder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Provides a simple data generation API for advancements.
 *
 * @author Jaxydog
 */
public class AdvancementGenerator extends FabricAdvancementProvider {

    private static @Nullable AdvancementGenerator instance;

    private final IdentifierMap<Builder> advancements = new IdentifierMap<>();

    public AdvancementGenerator(FabricDataOutput output) {
        super(output);

        assert instance == null;

        instance = this;
    }

    public static @NotNull AdvancementGenerator getInstance() {
        assert instance != null;

        return instance;
    }

    public void generate(Identifier identifier, Builder builder) {
        this.advancements.put(identifier, builder);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        this.advancements.forEach((identifier, builder) -> builder.build(consumer, identifier.toString()));
    }

}
