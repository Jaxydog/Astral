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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides a simple data generation API for language files.
 *
 * @author Jaxydog
 */
public class LanguageGenerator extends FabricLanguageProvider {

    private static @Nullable LanguageGenerator instance;

    private final List<Consumer<TranslationBuilder>> builders = new ObjectArrayList<>();

    public LanguageGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);

        assert instance == null;

        instance = this;
    }

    public static @NotNull LanguageGenerator getInstance() {
        assert instance != null;

        return instance;
    }

    public void combine(Path existing) {
        this.generate(builder -> {
            try {
                builder.add(existing);
            } catch (IOException exception) {
                Astral.LOGGER.warn("Unable to combine with specified language file '{}'", existing);
                Astral.LOGGER.warn(exception.getLocalizedMessage());
            }
        });
    }

    public void generate(Consumer<TranslationBuilder> builder) {
        this.builders.add(builder);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        this.builders.forEach(c -> {
            try {
                c.accept(builder);
            } catch (RuntimeException exception) {
                Astral.LOGGER.warn(exception.getLocalizedMessage());
            }
        });
    }

}
