package dev.jaxydog.utility.register;

import dev.jaxydog.register.Generated;
import dev.jaxydog.register.Registered;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiFunction;

public abstract class RegisteredMap<K, V extends Registered> implements Registered.All, Generated {

    private final String RAW_ID;
    private final BiFunction<String, K, V> CONSTRUCTOR;
    private final HashMap<K, V> INNER = new HashMap<>();

    public RegisteredMap(String rawId, BiFunction<String, K, V> constructor) {
        this.RAW_ID = rawId;
        this.CONSTRUCTOR = constructor;
    }

    protected abstract int compareKeys(K a, K b);

    public Collection<V> values() {
        this.constructIfEmpty();

        return this.INNER.values();
    }

    protected final void constructIfEmpty() {
        if (this.INNER.isEmpty()) this.construct();
    }

    private void construct() {
        // Generates and stores a new instance for each key.
        for (final K key : this.keys()) {
            final String id = this.getIdPath(key);
            final V value = this.CONSTRUCTOR.apply(id, key);

            this.INNER.put(key, value);
        }
    }

    public Set<K> keys() {
        return Set.of();
    }

    public abstract String getIdPath(K key);

    public final V get(K key) {
        this.constructIfEmpty();

        return this.INNER.get(key);
    }

    @Override
    public final String getRegistryIdPath() {
        return this.RAW_ID;
    }

    @Override
    public void register() {
        this.constructIfEmpty();

        this.keys().stream().sorted(this::compareKeys).forEach(key -> {
            if (this.get(key) instanceof final Common common) common.register();
        });
    }

    @Override
    public void registerClient() {
        this.constructIfEmpty();

        this.keys().stream().sorted(this::compareKeys).forEach(key -> {
            if (this.get(key) instanceof final Client client) client.registerClient();
        });
    }

    @Override
    public void registerServer() {
        this.constructIfEmpty();

        this.keys().stream().sorted(this::compareKeys).forEach(key -> {
            if (this.get(key) instanceof final Server server) server.registerServer();
        });
    }

    @Override
    public void generate() {
        this.constructIfEmpty();

        this.keys().stream().sorted(this::compareKeys).forEach(key -> {
            if (this.get(key) instanceof final Generated generated) generated.generate();
        });
    }

}
