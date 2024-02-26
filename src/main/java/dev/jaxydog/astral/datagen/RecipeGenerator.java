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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Provides a simple data generation API for recipes.
 *
 * @author Jaxydog
 */
public class RecipeGenerator extends FabricRecipeProvider {

    private static @Nullable RecipeGenerator instance;

    private final IdentifierMap<CraftingRecipeJsonBuilder> builders = new IdentifierMap<>();

    public RecipeGenerator(FabricDataOutput output) {
        super(output);

        assert instance == null;

        instance = this;
    }

    public static @NotNull RecipeGenerator getInstance() {
        assert instance != null;

        return instance;
    }

    public void generate(Identifier identifier, CraftingRecipeJsonBuilder builder) {
        this.builders.put(identifier, builder);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        this.builders.forEach((identifier, builder) -> builder.offerTo(exporter, identifier));
    }

}
