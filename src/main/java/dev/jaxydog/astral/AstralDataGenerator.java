package dev.jaxydog.astral;

import dev.jaxydog.astral.content.CustomContent;
import dev.jaxydog.astral.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.nio.file.Path;

/** The mod data generator entrypoint */
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

        CustomContent.INSTANCE.generate();
    }

}
