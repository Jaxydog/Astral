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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Provides a simple data generation API for block and item models.
 *
 * @author Jaxydog
 */
public class ModelGenerator extends FabricModelProvider {

    private static @Nullable ModelGenerator instance;

    private final List<Consumer<BlockStateModelGenerator>> blockGenerators = new ObjectArrayList<>();
    private final List<Consumer<ItemModelGenerator>> itemGenerators = new ObjectArrayList<>();

    public ModelGenerator(FabricDataOutput output) {
        super(output);

        assert instance == null;

        instance = this;
    }

    public static @NotNull ModelGenerator getInstance() {
        assert instance != null;

        return instance;
    }

    public void generateBlock(Consumer<BlockStateModelGenerator> consumer) {
        this.blockGenerators.add(consumer);
    }

    public void generateItem(Consumer<ItemModelGenerator> consumer) {
        this.itemGenerators.add(consumer);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        this.blockGenerators.forEach(c -> c.accept(blockStateModelGenerator));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        this.itemGenerators.forEach(c -> c.accept(itemModelGenerator));
    }

}
