/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023–2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.register;

import dev.jaxydog.astral.register.Registered.All;
import dev.jaxydog.astral.register.Registered.Generated;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;

/**
 * A generic map structure, backed by an {@link Object2ObjectOpenHashMap}, where all values implement the
 * {@link Registered} interface and are generated using a provided computation function.
 * <p>
 * This is most useful when generating a large amount of values at once. For a small number of items, it may be better
 * to instead just instantiate normally.
 *
 * @param <K> The map's key.
 * @param <V> The map's registerable value.
 *
 * @author Jaxydog
 * @see Registered
 */
public abstract class RegisteredMap<K, V extends Registered> implements All, Generated {

    /** The inner hash map that contains all generated keys and values. */
    protected final Map<K, V> innerMap = new Object2ObjectOpenHashMap<>();
    /** The base identifier path used to determine the identifier of each contained item. */
    private final String basePath;
    /** A callback function used to compute the value stored at an associated key. */
    private final BiFunction<@NotNull String, @NotNull K, V> computeCallback;

    /**
     * Creates a new registered map.
     *
     * @param basePath The base identifier path.
     * @param computeCallback The value computation callback.
     */
    protected RegisteredMap(@NotNull String basePath, BiFunction<@NotNull String, @NotNull K, V> computeCallback) {
        this.basePath = basePath;
        this.computeCallback = computeCallback;
    }

    /**
     * Returns the set of all expected keys.
     * <p>
     * The values within this set are fed into the computation callback to generate the values stored within this map.
     *
     * @return A set containing all expected keys.
     */
    public abstract Set<@NotNull K> keys();

    /**
     * Compares the provided keys to determine their registration order.
     * <p>
     * If {@code left} is lesser than {@code right}, a negative value should be returned. If {@code left} is greater
     * than {@code right}, a positive value should be returned. Otherwise, if both are equal, {@code 0} should be
     * returned.
     * <p>
     * For a simple example, assuming {@code <K>} is of type {@link Integer}, the value can be determined by evaluating
     * {@code right - left} for a simple natural ordering.
     *
     * @param left The left key.
     * @param right The right key.
     *
     * @return The result as defined in the method documentation.
     */
    protected abstract int compareKeys(@NotNull K left, @NotNull K right);

    /**
     * Returns an identifier path based off of the given key.
     *
     * @param key The value's key.
     *
     * @return A valid identifier path.
     */
    protected abstract String getPath(@NotNull K key);

    /**
     * Returns a value from within the map if it is assigned to the given key.
     * <p>
     * If the given key is present within the map, this is equivalent to calling
     * {@code Optional.ofNullable(map.getNullable(key))}.
     *
     * @param key The value's expected key.
     *
     * @return A possible value.
     */
    public Optional<V> get(@NotNull K key) {
        if (!this.innerMap.containsKey(key)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(this.getNullable(key));
        }
    }

    /**
     * Returns a value from within the map.
     *
     * @param key The value's expected key.
     *
     * @return A value, or {@code null} if the value is not present.
     */
    public @Nullable V getNullable(@NotNull K key) {
        return this.innerMap.get(key);
    }

    /**
     * Returns a set of all entries stored within this map.
     *
     * @return A set of all stored entries.
     */
    public Set<Entry<K, V>> entries() {
        return this.innerMap.entrySet();
    }

    /**
     * Returns a collection of the values stored within this map.
     *
     * @return A collection of stored values.
     */
    public Collection<V> values() {
        return this.innerMap.values();
    }

    /**
     * Computes all values stored within the map if they have not yet been created.
     * <p>
     * If the {@link #keys()} method returns an empty set, this method does nothing.
     */
    public void computeValuesIfEmpty() {
        if (this.innerMap.isEmpty()) this.computeValues();
    }

    /**
     * Computes all values stored within the map.
     * <p>
     * If any keys are stored within the map that are included within the set returned by {}, they will be overwritten.
     * <p>
     * If the {@link #keys()} method returns an empty set, this method does nothing.
     */
    protected void computeValues() {
        for (final K key : this.keys()) {
            final String path = this.getPath(key);
            final V value = this.computeCallback.apply(path, key);

            this.innerMap.put(key, value);
        }
    }

    /**
     * Returns a list of sorted values that match the expected registered type.
     *
     * @param type The expected type.
     *
     * @return A list of sorted values.
     */
    protected List<V> sortedValuesOfType(Class<? extends Registered> type) {
        return this.entries()
            .stream()
            .filter(e -> type.isInstance(e.getValue()))
            .sorted((l, r) -> this.compareKeys(l.getKey(), r.getKey()))
            .map(Entry::getValue)
            .toList();
    }

    @Override
    public String getRegistryPath() {
        return this.basePath;
    }

    @Override
    public void register() {
        this.computeValuesIfEmpty();
        this.sortedValuesOfType(Common.class).forEach(v -> ((Common) v).register());
    }

    @Override
    public void registerClient() {
        this.computeValuesIfEmpty();
        this.sortedValuesOfType(Client.class).forEach(v -> ((Client) v).registerClient());
    }

    @Override
    public void registerServer() {
        this.computeValuesIfEmpty();
        this.sortedValuesOfType(Server.class).forEach(v -> ((Server) v).registerServer());
    }

    @Override
    public void generate() {
        this.computeValuesIfEmpty();
        this.sortedValuesOfType(Generated.class).forEach(v -> ((Generated) v).generate());
    }

}
