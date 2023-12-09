package dev.jaxydog.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

public abstract class RegisterableMap<K, V extends Registerable> implements Registerable.All {

	private final String RAW_ID;
	private final HashMap<K, V> INNER = new HashMap<>(this.keys().size());

	public RegisterableMap(String rawId, BiFunction<String, K, V> constructor) {
		this.RAW_ID = rawId;

		for (final K key : this.keys()) {
			final String id = this.getRawId(key);
			final V value = constructor.apply(id, key);

			this.INNER.put(key, value);
		}
	}

	public abstract Set<K> keys();

	public abstract String getRawId(K key);

	protected abstract int compareKeys(K a, K b);

	public final Collection<V> values() {
		return this.INNER.values();
	}

	public final V get(K key) {
		return this.INNER.get(key);
	}

	@Override
	public final String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Registerable.Main main) {
				main.registerMain();
			}
		});
	}

	@Override
	public void registerClient() {
		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Registerable.Client client) {
				client.registerClient();
			}
		});
	}

	@Override
	public void registerServer() {
		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Registerable.Server server) {
				server.registerServer();
			}
		});
	}

	@Override
	public void registerDatagen(Pack pack) {
		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Registerable.Datagen datagen) {
				datagen.registerDatagen(pack);
			}
		});
	}

}
