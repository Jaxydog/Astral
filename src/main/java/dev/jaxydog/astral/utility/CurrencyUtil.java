/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023-2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.utility;

import com.google.gson.*;
import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.AstralGamerules;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * Provides implementations and resource loading for the mod's currency system.
 *
 * @author Jaxydog
 */
@NonExtendable
public interface CurrencyUtil {

    /** The NBT key used to determine whether a given item stack may be exchanged. */
    String EXCHANGE_KEY = "Exchange";

    /**
     * Returns whether the given item stack may be exchanged.
     * <p>
     * This is checked using the {@value #EXCHANGE_KEY} NBT tag. If this tag is not present, this method will always
     * return true.
     *
     * @param stack The target item stack.
     *
     * @return Whether the current stack may be exchanged.
     */
    static boolean canExchange(ItemStack stack) {
        final NbtCompound nbtCompound = stack.getNbt();

        return nbtCompound == null || !nbtCompound.contains(EXCHANGE_KEY) || nbtCompound.getBoolean(EXCHANGE_KEY);
    }

    /**
     * Drops rewards to the provided player entity.
     *
     * @param player The target player entity.
     * @param inventory The target player inventory.
     * @param rolls The number of attempts at rolling a reward.
     */
    static void dropRewards(PlayerEntity player, PlayerInventory inventory, int rolls) {
        if (Reward.REWARDS.isEmpty()) return;

        final Random random = player.getRandom();
        final double rewardChance = player.getWorld().getGameRules().get(AstralGamerules.CURRENCY_REWARD_CHANCE).get();

        int count = 0;

        // Try to roll a reward `rolls` times, with a `rewardChance` chance of success per iteration.
        for (int iteration = 0; iteration < rolls; iteration += 1) {
            if (random.nextDouble() <= rewardChance) count += 1;
        }

        final List<Reward> rewards = Reward.getRandom(count);

        for (final Reward reward : rewards) {
            inventory.offerOrDrop(reward.getItem().getDefaultStack());
        }
    }

    /**
     * Automatically exchanges currency items within the provided inventory.
     * <p>
     * This method does nothing if run on the client.
     *
     * @param player The target player entity.
     * @param inventory The target player inventory.
     */
    static void tryExchange(PlayerEntity player, PlayerInventory inventory) {
        if (player.getWorld().isClient()) return;

        final List<ItemStack> stacks = new ObjectArrayList<>();

        // Grab a reference of all stacks within the inventory.
        for (int slot = 0; slot < inventory.size(); slot += 1) {
            final ItemStack stack = inventory.getStack(slot);

            if (!stack.isEmpty()) stacks.add(stack);
        }

        // Finds the unit value based on the streamed stack, yielding a stack-unit pair if it was found.
        final List<Pair<ItemStack, Unit>> units = stacks.stream()
            .flatMap(s -> Unit.UNITS.findByItem(s).stream().map(u -> new Pair<>(s, u)))
            .toList();

        tryExchangeUnits(player, inventory, units);

        // Re-compute the stack list to include the potentially updated data.
        stacks.clear();

        for (int slot = 0; slot < inventory.size(); slot += 1) {
            final ItemStack stack = inventory.getStack(slot);

            if (!stack.isEmpty()) stacks.add(stack);
        }

        // Finds the reward value based on the streamed stack, yielding a pair if it could be found.
        final List<Pair<ItemStack, Reward>> rewards = stacks.stream()
            .flatMap(s -> Reward.REWARDS.findByItem(s).stream().map(u -> new Pair<>(s, u)))
            .toList();

        tryExchangeRewards(player, inventory, rewards);
    }

    /**
     * Automatically exchanges currency unit items within the provided inventory.
     * <p>
     * This method does nothing if run on the client.
     *
     * @param player The target player entity.
     * @param inventory The target player inventory.
     * @param pairs Pairs of item stacks and their associated units.
     */
    static void tryExchangeUnits(PlayerEntity player, PlayerInventory inventory, List<Pair<ItemStack, Unit>> pairs) {
        final Map<Unit, Integer> unitCounts = new Object2ObjectOpenHashMap<>(pairs.size());

        // Filter out pairs that cannot be exchanged and count the total number of unit items.
        for (final Pair<ItemStack, Unit> pair : pairs) {
            final ItemStack stack = pair.getLeft();
            final Unit unit = pair.getRight();

            if (canExchange(stack)) {
                unitCounts.merge(unit, stack.getCount(), Integer::sum);
            }
        }

        unitCounts.forEach((unit, count) -> {
            final Optional<Entry<Identifier, Unit>> maybeNext = unit.next(true);

            if (maybeNext.isEmpty()) return;

            // Default to using the mod's default namespace in case the unit isn't registered.
            final String thisNamespace = Unit.UNITS.getId(unit).map(Identifier::getNamespace).orElse(Astral.MOD_ID);
            final String nextNamespace = maybeNext.get().getKey().getNamespace();
            final Unit next = maybeNext.get().getValue();
            // Calculate the "price" (conversion rate) and total allowed stack instances.
            final int price;

            // Make sure we use the converted value if this is from a different namespace.
            if (thisNamespace.equals(nextNamespace)) {
                price = next.value() / unit.value();
            } else {
                price = next.value() / unit.exchanges().get(nextNamespace);
            }

            // Should never happen, but it's better to add a safeguard in case I do something stupid.
            if (price == 0) return;

            final int total = count / price;

            if (total == 0) return;

            // Remove all consumed unit items.
            inventory.remove(
                s -> s.getItem().equals(unit.getItem()) && canExchange(s),
                total * price,
                player.playerScreenHandler.getCraftingInput()
            );

            final ItemStack stack = next.getItem().getDefaultStack();

            stack.setCount(total);
            inventory.offerOrDrop(stack);

            if (next.hasDrops()) dropRewards(player, inventory, total);
        });
    }

    /**
     * Automatically exchanges currency reward items within the provided inventory.
     * <p>
     * This method does nothing if run on the client.
     *
     * @param player The target player entity.
     * @param inventory The target player inventory.
     * @param pairs Pairs of item stacks and their associated rewards.
     */
    static void tryExchangeRewards(
        PlayerEntity player, PlayerInventory inventory, List<Pair<ItemStack, Reward>> pairs
    ) {
        final Map<Reward, Integer> rewardCounts = new Object2ObjectOpenHashMap<>(pairs.size());
        final List<Skeleton> possibleSkeletons = new ObjectArrayList<>();

        // Filter out pairs that cannot be exchanged and count the total number of reward items.
        for (final Pair<ItemStack, Reward> pair : pairs) {
            final ItemStack stack = pair.getLeft();
            final Reward reward = pair.getRight();

            if (canExchange(stack)) {
                rewardCounts.merge(reward, stack.getCount(), Integer::sum);
            }
        }

        // Grab a list of all possible skeletons.
        for (final Skeleton skeleton : Skeleton.SKELETONS.values()) {
            if (skeleton.hasRequirements(rewardCounts)) possibleSkeletons.add(skeleton);
        }

        final Map<Reward, Integer> removedCounts = new Object2ObjectOpenHashMap<>(rewardCounts.size());
        final Map<Skeleton, Integer> skeletons = new Object2ObjectArrayMap<>(possibleSkeletons.size());

        // Repeatedly loop through all possible skeletons, removing and filtering them out when uncraftable until empty.
        while (!possibleSkeletons.isEmpty()) {
            final List<Skeleton> removed = new ObjectArrayList<>();

            for (final Skeleton skeleton : possibleSkeletons) {
                // If craftable, add a reward and retain the skeleton entry.
                if (skeleton.hasRequirements(rewardCounts)) {
                    for (final Reward reward : skeleton.getRequirements()) {
                        removedCounts.merge(reward, 1, Integer::sum);
                        rewardCounts.computeIfPresent(reward, (r, n) -> n - 1);
                    }

                    skeletons.merge(skeleton, 1, Integer::sum);
                } else {
                    removed.add(skeleton);
                }
            }

            possibleSkeletons.removeAll(removed);
        }

        // Remove all skeleton ingredients.
        removedCounts.forEach(((reward, count) -> inventory.remove(
            s -> s.getItem().equals(reward.getItem()) && canExchange(s),
            count,
            player.playerScreenHandler.getCraftingInput()
        )));
        // Then give the player all crafted skeletons.
        skeletons.forEach((skeleton, count) -> {
            final ItemStack stack = skeleton.getItem().getDefaultStack();

            stack.setCount(count);

            inventory.offerOrDrop(stack);
        });
    }

    /**
     * A value that represents an associated item.
     *
     * @author Jaxydog
     */
    interface ItemRepresenting {

        /**
         * Returns the associated item identifier, or {@code minecraft:air} if this value's identifier is invalid.
         *
         * @return The associated item identifier.
         */
        Identifier getItemId();

        /**
         * Returns the associated item, or {@code minecraft:air} if this value's identifier is invalid.
         *
         * @return The associated item.
         */
        default Item getItem() {
            return Registries.ITEM.get(this.getItemId());
        }

    }

    /**
     * A class that maps item identifiers to an associated type.
     *
     * @param <T> The type stored within the internal map.
     *
     * @author Jaxydog
     */
    class ItemMap<T extends ItemRepresenting> {

        private final Map<Identifier, T> map = new Object2ObjectOpenHashMap<>();

        /**
         * Loads the provided values into the map, clearing the previously stored entries.
         * <p>
         * If a value's item identifier resolves to {@code minecraft:air}, the value will be ignored.
         *
         * @param values The values to add into the map.
         *
         * @return The total number of successfully added values.
         */
        public int load(Map<Identifier, T> values) {
            this.map.clear();
            this.map.putAll(values);

            return this.map.size();
        }

        /**
         * Returns the value associated with the provided identifier.
         *
         * @param identifier The value's actual identifier.
         *
         * @return The value associated with the identifier.
         */
        public Optional<T> get(Identifier identifier) {
            return Optional.ofNullable(this.map.get(identifier));
        }

        /**
         * Finds and returns the value associated with the provided item identifier.
         *
         * @param identifier The expected item identifier.
         *
         * @return The value associated with the provided item identifier.
         */
        public Optional<T> findByItem(Identifier identifier) {
            return this.map.values().stream().filter(t -> t.getItemId().equals(identifier)).findFirst();
        }

        /**
         * Finds and returns the value associated with the provided item.
         *
         * @param item The expected item.
         *
         * @return The value associated with the provided item.
         */
        public Optional<T> findByItem(Item item) {
            return this.findByItem(Registries.ITEM.getId(item));
        }

        /**
         * Finds and returns the value associated with the provided item stack.
         *
         * @param stack The expected item stack.
         *
         * @return The value associated with the provided item stack.
         */
        public Optional<T> findByItem(ItemStack stack) {
            return this.findByItem(stack.getItem());
        }

        /**
         * Finds and returns the identifier of the provided value.
         *
         * @param value The value to get the identifier of.
         *
         * @return The identifier associated with the value.
         */
        public Optional<Identifier> getId(T value) {
            return this.map.entrySet().stream().filter(e -> e.getValue().equals(value)).map(Entry::getKey).findFirst();
        }

        /**
         * Returns the total number of registered values within the inner map.
         *
         * @return The total number of registered values.
         */
        public int size() {
            return this.map.size();
        }

        /**
         * Returns whether the inner map is empty.
         *
         * @return If the map is empty.
         */
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        /**
         * Returns a set containing the registered values and their identifiers.
         *
         * @return The registered entries.
         */
        public Set<Entry<Identifier, T>> entrySet() {
            return this.map.entrySet();
        }

        /**
         * Returns a collection containing the registered identifiers.
         *
         * @return The registered values.
         */
        public Collection<T> values() {
            return this.map.values();
        }

    }

    /**
     * A base unit of currency.
     *
     * @param itemIdentifier The identifier of this unit's associated item representation.
     * @param value The "value" of this currency, compared to other currency units.
     * @param hasDrops Whether this unit of currency should support current reward drops.
     *
     * @author Jaxydog
     */
    record Unit(Identifier itemIdentifier, int value, boolean hasDrops, Map<String, Integer> exchanges)
        implements ItemRepresenting, Comparable<Unit> {

        public static final ItemMap<Unit> UNITS = new ItemMap<>();
        private static final Comparator<Unit> COMPARATOR = Comparator.comparingInt(Unit::value);

        /**
         * Parses a JSON object and creates a new unit from the resolved data.
         *
         * @param itemIdentifier The associated item identifier.
         * @param object The JSON object.
         *
         * @return A new unit.
         */
        public static Unit parse(Identifier itemIdentifier, JsonObject object) {
            final int value = JsonHelper.getInt(object, "value");

            if (value <= 0) {
                throw new JsonParseException("Expected a positive non-zero value");
            }

            final boolean hasDrops = JsonHelper.getBoolean(object, "drops", false);
            final Map<String, Integer> exchanges;

            if (object.has("exchanges")) {
                final JsonObject exchangesObject = JsonHelper.getObject(object, "exchanges");

                exchanges = new Object2IntOpenHashMap<>(exchangesObject.size());

                // Parse out conversion rates for other namespaces.
                for (final Entry<String, JsonElement> entry : exchangesObject.entrySet()) {
                    if (!entry.getValue().isJsonPrimitive()) continue;

                    final int effectiveValue = entry.getValue().getAsJsonPrimitive().getAsInt();

                    if (effectiveValue <= 0) {
                        throw new JsonParseException("Expected a positive non-zero exchange value");
                    }

                    exchanges.put(entry.getKey(), effectiveValue);
                }
            } else {
                exchanges = Map.of();
            }

            return new Unit(itemIdentifier, value, hasDrops, exchanges);
        }

        /**
         * Returns the next unit of currency by value.
         *
         * @param exactMultiple Whether the unit returned should hold an exact multiple of this unit's value.
         *
         * @return The next unit determined by value.
         */
        public Optional<Entry<Identifier, Unit>> next(boolean exactMultiple) {
            // Default to using the mod's default namespace in case the unit isn't registered.
            final String thisNamespace = UNITS.getId(this).map(Identifier::getNamespace).orElse(Astral.MOD_ID);

            final Stream<Entry<Identifier, Unit>> stream = UNITS.entrySet()
                .stream()
                .filter(e -> !e.getValue().equals(this))
                .flatMap(entry -> {
                    final String namespace = entry.getKey().getNamespace();

                    // Make sure we use the converted value if this is from a different namespace.
                    if (this.exchanges().containsKey(namespace)) {
                        final int value = this.exchanges().get(namespace);

                        return Stream.of(entry).filter(e -> e.getValue().value() % value == 0);
                    } else if (namespace.equals(thisNamespace)) {
                        return Stream.of(entry).filter(e -> e.getValue().value() % this.value() == 0);
                    } else {
                        return Stream.empty();
                    }
                });

            if (!exactMultiple) return stream.min(Comparator.comparingInt(e -> e.getValue().value()));

            return stream.filter(entry -> {
                // Make sure we use the converted value if this is from a different namespace.
                if (entry.getKey().getNamespace().equals(thisNamespace)) {
                    return entry.getValue().value() % this.value() == 0;
                } else {
                    final int value = this.exchanges().get(entry.getKey().getNamespace());

                    return entry.getValue().value() % value == 0;
                }
            }).min(Comparator.comparingInt(e -> e.getValue().value()));
        }

        @Override
        public Identifier getItemId() {
            if (Registries.ITEM.containsId(this.itemIdentifier())) {
                return this.itemIdentifier();
            } else {
                return Registries.ITEM.getDefaultId();
            }
        }

        @Override
        public int compareTo(@NotNull Unit other) {
            return COMPARATOR.compare(this, other);
        }

    }

    /**
     * An item given out as a reward for interacting with the currency system.
     *
     * @param itemIdentifier The identifier of this reward's associated item representation.
     * @param weight The weight used to determine this reward's chance of dropping.
     *
     * @author Jaxydog
     */
    record Reward(Identifier itemIdentifier, int weight) implements ItemRepresenting {

        public static final ItemMap<Reward> REWARDS = new ItemMap<>();

        /**
         * Parses a JSON object and creates a new reward from the resolved data.
         *
         * @param itemIdentifier The associated item identifier.
         * @param object The JSON object.
         *
         * @return A new reward.
         */
        public static Reward parse(Identifier itemIdentifier, JsonObject object) {
            final int weight = JsonHelper.getInt(object, "weight");

            if (weight <= 0) {
                throw new JsonParseException("Expected a positive non-zero weight");
            }

            return new Reward(itemIdentifier, weight);
        }

        /**
         * Returns random registered rewards, with the random chances determined by their associated weights.
         *
         * @param count The total number of rewards to select.
         *
         * @return A list of random rewards.
         */
        public static List<Reward> getRandom(int count) {
            final int elements = Math.max(count, REWARDS.size());
            final List<Reward> output = new ObjectArrayList<>(count);
            final WeightedList<Reward> rewards = new WeightedList<>();

            REWARDS.values().forEach(r -> rewards.add(r, r.weight()));

            // This is done instead of `limit(n).toList()` to allow for duplicate elements.
            for (int iteration = 0; iteration < elements; iteration += 1) {
                rewards.shuffle().stream().findFirst().ifPresent(output::add);
            }

            return output;
        }

        @Override
        public Identifier getItemId() {
            if (Registries.ITEM.containsId(this.itemIdentifier())) {
                return this.itemIdentifier();
            } else {
                return Registries.ITEM.getDefaultId();
            }
        }

    }

    /**
     * An item crafted using multiple currency rewards.
     *
     * @param itemIdentifier The identifier of this skeleton's associated item representation.
     * @param requires The items required to craft this skeleton.
     *
     * @author Jaxydog
     */
    record Skeleton(Identifier itemIdentifier, List<Identifier> requires) implements ItemRepresenting {

        public static final ItemMap<Skeleton> SKELETONS = new ItemMap<>();

        /**
         * Parses a JSON object and creates a new skeleton from the resolved data.
         *
         * @param itemIdentifier The associated item identifier.
         * @param object The JSON object.
         *
         * @return A new skeleton.
         */
        public static Skeleton parse(Identifier itemIdentifier, JsonObject object) {
            final JsonArray array = JsonHelper.getArray(object, "cost");
            final List<Identifier> requirements = new ObjectArrayList<>(array.size());

            // Parse a list of identifiers for requirements, ignoring invalid ones.
            for (JsonElement element : array) {
                final String string = element.getAsString();
                final Identifier identifier = Identifier.tryParse(string);

                if (identifier != null) {
                    requirements.add(identifier);
                } else {
                    throw new JsonSyntaxException("Invalid identifier '" + string + "'");
                }
            }

            // Prevent the skeleton from generating if its requirement list is empty.
            // This prevents potential infinite loops.
            if (requirements.isEmpty()) {
                throw new JsonParseException("Expected a non-empty cost array");
            }

            return new Skeleton(itemIdentifier, requirements);
        }

        /**
         * Returns a list of required reward entries.
         * <p>
         * If a requirement's identifier is not found it will be ignored.
         *
         * @return A list of required rewards.
         */
        public List<Reward> getRequirements() {
            return this.requires().stream().flatMap(i -> Reward.REWARDS.get(i).stream()).toList();
        }

        /**
         * Returns whether the provided map contains the required number of rewards to craft this skeleton.
         *
         * @param rewardCounts A map of rewards and their counts.
         *
         * @return Whether the requirements are met.
         */
        public boolean hasRequirements(Map<Reward, Integer> rewardCounts) {
            final Map<Reward, Integer> requirementCounts = new Object2ObjectArrayMap<>(rewardCounts.size());
            final List<Reward> requirements = this.getRequirements();

            // Grab the total amount of requirements.
            for (final Reward reward : requirements) {
                requirementCounts.merge(reward, 1, Integer::sum);
            }

            // Ensure there's at least enough to craft one skeleton.
            return requirementCounts.entrySet().stream().allMatch(e -> {
                if (!rewardCounts.containsKey(e.getKey())) return false;

                return rewardCounts.get(e.getKey()) >= e.getValue();
            });
        }

        @Override
        public Identifier getItemId() {
            if (Registries.ITEM.containsId(this.itemIdentifier())) {
                return this.itemIdentifier();
            } else {
                return Registries.ITEM.getDefaultId();
            }
        }

    }

}
