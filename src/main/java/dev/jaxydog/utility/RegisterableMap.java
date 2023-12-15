package dev.jaxydog.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

public abstract class RegisterableMap<K, V extends Registerable> implements Registerable.All {

	private final String RAW_ID;
	private final BiFunction<String, K, V> CONSTRUCTOR;
	private final HashMap<K, V> INNER = new HashMap<>();

	public RegisterableMap(String rawId, BiFunction<String, K, V> constructor) {
		this.RAW_ID = rawId;
		this.CONSTRUCTOR = constructor;
	}

	public abstract String getRawId(K key);

	protected abstract int compareKeys(K a, K b);

	public Set<K> keys() {
		return Set.of();
	}

	public Collection<V> values() {
		this.constructIfEmpty();

		return this.INNER.values();
	}

	protected final void constructIfEmpty() {
		if (this.INNER.isEmpty()) {
			this.construct();
		}
	}

	private final void construct() {
		for (final K key : this.keys()) {
			final String id = this.getRawId(key);
			final V value = this.CONSTRUCTOR.apply(id, key);

			this.INNER.put(key, value);
		}
	}

	public final V get(K key) {
		this.constructIfEmpty();

		return this.INNER.get(key);
	}

	@Override
	public final String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		this.constructIfEmpty();

		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Main main) {
				main.registerMain();
			}
		});
	}

	@Override
	public void registerClient() {
		this.constructIfEmpty();

		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Client client) {
				client.registerClient();
			}
		});
	}

	@Override
	public void registerServer() {
		this.constructIfEmpty();

		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Server server) {
				server.registerServer();
			}
		});
	}

	@Override
	public void registerDatagen(Pack pack) {
		this.constructIfEmpty();

		this.keys().stream().sorted(this::compareKeys).forEach(key -> {
			if (this.get(key) instanceof final Datagen datagen) {
				datagen.registerDatagen(pack);
			}
		});
	}

}
