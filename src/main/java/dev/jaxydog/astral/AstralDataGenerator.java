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

package dev.jaxydog.astral;

import dev.jaxydog.astral.content.AstralContent;
import dev.jaxydog.astral.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.nio.file.Path;

/**
 * The mod's data generation entrypoint.
 * <p>
 * This class is initialized during the `runDatagen` Gradle process.
 * <p>
 * It should be assumed that prior to the invocation of {@link #onInitializeDataGenerator(FabricDataGenerator)}, nothing
 * has been properly loaded.
 * <p>
 * Please modify with a healthy dose of caution!
 *
 * @author Jaxydog
 * @see Astral
 * @see AstralClient
 * @see AstralServer
 */
public final class AstralDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        final FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(AdvancementGenerator::new);
        new LootTableGenerator(pack);
        pack.addProvider(ModelGenerator::new);
        new TagGenerator(pack);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(LanguageGenerator::new);

        if (JarAccess.canLoad()) {
            new TextureGenerator(pack);
        }

        final Path languagePath = generator.getModContainer()
            .findPath("assets/%s/lang/en_us.json".formatted(Astral.MOD_ID))
            .orElseThrow();

        LanguageGenerator.getInstance().combine(languagePath);

        AstralContent.INSTANCE.generate();
    }

}
