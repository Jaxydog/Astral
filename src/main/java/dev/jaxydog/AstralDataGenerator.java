package dev.jaxydog;

import dev.jaxydog.content.CustomContent;
import dev.jaxydog.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/** The mod data generator entrypoint */
public final class AstralDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        final FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(AdvancementGenerator::new);
        LootTableGenerator.addAllProviders(pack);
        pack.addProvider(ModelGenerator::new);
        new TagGenerator(pack);
        pack.addProvider(RecipeGenerator::new);

        CustomContent.INSTANCE.generate();
    }

}
