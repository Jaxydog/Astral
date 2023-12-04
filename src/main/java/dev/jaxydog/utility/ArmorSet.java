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
		for (final Type type : Type.values()) {
			final String rawId = baseRawId + "_" + type.getName();
			final T value = constructor.apply(rawId, type);

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
		for (final Type type : Type.values()) {
			final T value = this.get(type);

			if (value instanceof final Client client) {
				client.registerClient();
			}
		}
	}

	@Override
	public final void registerMain() {
		for (final Type type : Type.values()) {
			final T value = this.get(type);

			if (value instanceof final Main main) {
				main.registerMain();
			}
		}
	}

	@Override
	public final void registerServer() {
		for (final Type type : Type.values()) {
			final T value = this.get(type);

			if (value instanceof final Server server) {
				server.registerServer();
			}
		}
	}

	@Override
	public final void registerDatagen(FabricDataGenerator generator) {
		for (final Type type : Type.values()) {
			final T value = this.get(type);

			if (value instanceof final Datagen dataGenerator) {
				dataGenerator.registerDatagen(generator);
			}
		}
	}

}
