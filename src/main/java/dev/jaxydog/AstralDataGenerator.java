package dev.jaxydog;

import dev.jaxydog.content.CustomContent;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/** The mod data generator entrypoint */
public final class AstralDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		CustomContent.INSTANCE.registerDatagen(generator);
	}
}
