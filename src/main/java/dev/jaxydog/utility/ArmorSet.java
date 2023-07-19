package dev.jaxydog.utility;

import java.util.HashMap;
import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.ArmorItem.Type;

/** Utility class for auto-generating values for each armor slot */
public final class ArmorSet<T extends Registerable> implements Registerable.All {

	/** The set's internal map that contains the created value for each dye color */
	private final HashMap<Type, T> MAP = new HashMap<>(Type.values().length);

	public ArmorSet(String baseRawId, BiFunction<String, Type, T> constructor) {
		for (Type type : Type.values()) {
			String rawId = baseRawId + "_" + type.getName();
			T value = constructor.apply(rawId, type);

			this.MAP.put(type, value);
		}
	}

	/** Returns the value associated with the given color */
	public final T get(Type type) {
		return this.MAP.get(type);
	}

	@Override
	public final String getRawId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void registerClient() {
		for (Type type : Type.values()) {
			T value = this.get(type);

			if (value instanceof Client client) {
				client.registerClient();
			}
		}
	}

	@Override
	public final void registerMain() {
		for (Type type : Type.values()) {
			T value = this.get(type);

			if (value instanceof Main main) {
				main.registerMain();
			}
		}
	}

	@Override
	public final void registerServer() {
		for (Type type : Type.values()) {
			T value = this.get(type);

			if (value instanceof Server server) {
				server.registerServer();
			}
		}
	}

	@Override
	public final void registerDatagen(FabricDataGenerator generator) {
		for (Type type : Type.values()) {
			T value = this.get(type);

			if (value instanceof Datagen dataGenerator) {
				dataGenerator.registerDatagen(generator);
			}
		}
	}
}
