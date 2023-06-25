package dev.jaxydog.utility;

import dev.jaxydog.utility.register.Registerable;
import java.util.HashMap;
import java.util.function.BiFunction;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.util.DyeColor;

/**
 * Utility class for auto-generating values for each dye color --- This is similar to how dyed
 * amethyst blocks are generated within the old Moonlight mod, only made to be much more generic
 */
public final class DyeableSet<T extends Registerable> implements Registerable.All {

	/** The set's internal map that contains the created value for each dye color */
	private final HashMap<DyeColor, T> MAP = new HashMap<>(DyeColor.values().length);

	public DyeableSet(String baseRawId, BiFunction<String, DyeColor, T> constructor) {
		for (DyeColor color : DyeColor.values()) {
			String rawId = color.asString() + "_" + baseRawId;
			T value = constructor.apply(rawId, color);

			this.MAP.put(color, value);
		}
	}

	/** Returns the value associated with the given color */
	public final T get(DyeColor color) {
		return this.MAP.get(color);
	}

	@Override
	public final String getRawId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void registerClient() {
		for (T value : this.MAP.values()) {
			if (value instanceof Client client) {
				client.registerClient();
			}
		}
	}

	@Override
	public final void registerMain() {
		for (T value : this.MAP.values()) {
			if (value instanceof Main main) {
				main.registerMain();
			}
		}
	}

	@Override
	public final void registerServer() {
		for (T value : this.MAP.values()) {
			if (value instanceof Server server) {
				server.registerServer();
			}
		}
	}

	@Override
	public final void registerDatagen(FabricDataGenerator generator) {
		for (T value : this.MAP.values()) {
			if (value instanceof Datagen dataGenerator) {
				dataGenerator.registerDatagen(generator);
			}
		}
	}
}
