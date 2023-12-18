package dev.jaxydog.utility;

import com.google.gson.*;
import dev.jaxydog.Astral;
import dev.jaxydog.content.CustomGamerules;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/** Provides implementations and resource loading for the currency system */
@NonExtendable
public interface CurrencyUtil {

	String EXCHANGE_KEY = "Exchange";

	/** Automatically exchanges currency items within the inventory */
	static void tryExchange(PlayerEntity entity, PlayerInventory inventory) {
		if (entity.getWorld().isClient()) {
			return;
		}

		final ArrayList<ItemStack> items = new ArrayList<>();

		for (int slot = 0; slot < inventory.size(); slot += 1) {
			final ItemStack stack = inventory.getStack(slot);

			if (!stack.isEmpty()) {
				items.add(stack);
			}
		}

		final List<Pair<ItemStack, Unit>> units = items.stream()
			.map(s -> new Pair<>(s, Unit.find(s)))
			.filter(p -> p.getRight().isPresent())
			.map(p -> new Pair<>(p.getLeft(), p.getRight().get()))
			.toList();

		tryExchangeUnits(entity, inventory, units);

		final List<Pair<ItemStack, Reward>> rewards = items.stream()
			.map(s -> new Pair<>(s, Reward.find(s)))
			.filter(p -> p.getRight().isPresent())
			.map(p -> new Pair<>(p.getLeft(), p.getRight().get()))
			.toList();

		tryExchangeRewards(entity, inventory, rewards);
	}

	/** Automatically exchanges currency items within the inventory */
	static void tryExchangeUnits(
		PlayerEntity entity, PlayerInventory inventory, List<Pair<ItemStack, Unit>> items
	) {
		final HashMap<Unit, Integer> units = new HashMap<>();

		for (final Pair<ItemStack, Unit> pair : items) {
			final ItemStack stack = pair.getLeft();
			final Unit unit = pair.getRight();

			if (canExchange(stack)) units.merge(unit, stack.getCount(), Integer::sum);
		}

		for (final Entry<Unit, Integer> entry : units.entrySet()) {
			final Unit unit = entry.getKey();
			final int count = entry.getValue();
			final Optional<Unit> optionalNext = unit.nextMultiple();

			if (optionalNext.isEmpty()) continue;

			final Unit next = optionalNext.get();
			final ItemStack stack = next.getItem().getDefaultStack();
			final int price = next.value() / unit.value();
			final int crafted = count / price;

			inventory.remove(
				s -> s.getItem() == unit.getItem() && canExchange(s),
				crafted * price,
				entity.playerScreenHandler.getCraftingInput()
			);

			stack.setCount(crafted);
			inventory.offerOrDrop(stack);

			if (next.drops()) dropRewards(entity, inventory, crafted);
		}
	}

	/** Automatically exchanges currency items within the inventory */
	static void tryExchangeRewards(
		PlayerEntity entity, PlayerInventory inventory, List<Pair<ItemStack, Reward>> items
	) {
		final HashMap<Reward, Integer> rewards = new HashMap<>(items.size());
		final HashMap<Reward, Integer> removed = new HashMap<>(items.size());
		final ArrayList<Skeleton> exchanged = new ArrayList<>();

		for (final Pair<ItemStack, Reward> pair : items) {
			final ItemStack stack = pair.getLeft();
			final Reward reward = pair.getRight();

			if (canExchange(stack)) {
				rewards.merge(reward, stack.getCount(), Integer::sum);
			}
		}

		for (final Skeleton skeleton : Skeleton.MAP.values()) {
			final Map<Reward, Integer> price = skeleton.getPrice()
				.stream()
				.collect(Collectors.toMap(s -> s, s -> 1, Integer::sum));

			if (price.isEmpty() || !price.entrySet()
				.stream()
				.allMatch(e -> rewards.containsKey(e.getKey()) && rewards.get(e.getKey()) >= e.getValue())) {
				continue;
			}

			exchanged.add(skeleton);
			price.forEach((key, value) -> removed.merge(key, value, Integer::sum));
		}

		removed.forEach((reward, count) -> inventory.remove(
			s -> s.getItem() == reward.getItem() && canExchange(s),
			count,
			entity.playerScreenHandler.getCraftingInput()
		));
		exchanged.forEach(skeleton -> inventory.offerOrDrop(skeleton.getItem().getDefaultStack()));
	}

	/** Returns whether the given stack may be exchanged */
	static boolean canExchange(ItemStack stack) {
		final NbtCompound nbt = stack.getNbt();

		return nbt == null || !nbt.contains(EXCHANGE_KEY) || nbt.getBoolean(EXCHANGE_KEY);
	}

	/** Drops rewards */
	static void dropRewards(PlayerEntity entity, PlayerInventory inventory, int rolls) {
		if (Reward.isEmpty()) return;

		final Random random = entity.getRandom();
		final double rewardChance = entity.getWorld().getGameRules().get(CustomGamerules.CURRENCY_REWARD_CHANCE).get();
		int total = 0;

		for (int iteration = 0; iteration < rolls; iteration += 1) {
			final double generated = random.nextDouble();

			if (generated < rewardChance) total += 1;
		}

		for (int remaining = total; remaining > 0; remaining -= 1) {
			Reward.random().ifPresent(reward -> inventory.offerOrDrop(reward.getItem().getDefaultStack()));
		}
	}

	/** A currency unit */
	record Unit(Identifier item, int value, boolean drops) {

		/** The inner map */
		private static final HashMap<Identifier, Unit> MAP = new HashMap<>(0);

		/** Returns the currency unit associated with the item */
		public static Optional<Unit> find(ItemStack item) {
			return find(item.getItem());
		}

		/** Returns the currency unit associated with the item */
		public static Optional<Unit> find(Item item) {
			return find(Registries.ITEM.getId(item));
		}

		/** Returns the currency unit associated with the item's identifier */
		public static Optional<Unit> find(Identifier item) {
			return MAP.values().stream().filter(u -> u.item().equals(item)).findFirst();
		}

		/** Loads the given currency units into the internal map */
		public static void load(Map<Identifier, Unit> units) {
			MAP.clear();
			MAP.putAll(units);
			Astral.LOGGER.info("Loaded " + units.size() + " currency units");
		}

		/** Returns whether the unit map is empty */
		public static boolean isEmpty() {
			return MAP.isEmpty();
		}

		/** Returns the associated item */
		public Item getItem() {
			return Registries.ITEM.get(this.item());
		}

		/** Returns the unit with the next highest value */
		public Optional<Unit> next() {
			return MAP.values()
				.stream()
				.sorted(Comparator.comparingInt(Unit::value))
				.filter(u -> u.value() > this.value())
				.findFirst();
		}

		/** Returns the unit with the next highest value */
		public Optional<Unit> nextMultiple() {
			return MAP.values()
				.stream()
				.sorted(Comparator.comparingInt(Unit::value))
				.filter(u -> u.value() > this.value())
				.filter(u -> u.value() % this.value() == 0)
				.findFirst();
		}

		/** Returns the unit with the previous highest value */
		public Optional<Unit> last() {
			return MAP.values()
				.stream()
				.sorted((a, b) -> Integer.compare(b.value(), a.value()))
				.filter(u -> u.value() < this.value())
				.findFirst();
		}

		/** Returns the unit with the previous highest value */
		public Optional<Unit> lastMultiple() {
			return MAP.values()
				.stream()
				.sorted((a, b) -> Integer.compare(b.value(), a.value()))
				.filter(u -> u.value() < this.value())
				.filter(u -> this.value() % u.value() == 0)
				.findFirst();
		}

	}

	/** A currency reward */
	record Reward(Identifier item, int weight) {

		/** The inner map */
		private static final HashMap<Identifier, Reward> MAP = new HashMap<>(0);

		/** Returns the currency reward associated with the item */
		public static Optional<Reward> find(ItemStack item) {
			return find(item.getItem());
		}

		/** Returns the currency reward associated with the item */
		public static Optional<Reward> find(Item item) {
			return find(Registries.ITEM.getId(item));
		}

		/** Returns the currency reward associated with the item's identifier */
		public static Optional<Reward> find(Identifier item) {
			return MAP.values().stream().filter(r -> r.item().equals(item)).findFirst();
		}

		/** Returns a random reward */
		public static Optional<Reward> random() {
			final WeightedList<Reward> rewards = new WeightedList<>();

			MAP.values().forEach(r -> rewards.add(r, r.weight()));

			return rewards.shuffle().stream().findFirst();
		}

		/** Loads the given currency rewards into the internal map */
		public static void load(Map<Identifier, Reward> rewards) {
			MAP.clear();
			MAP.putAll(rewards);
			Astral.LOGGER.info("Loaded " + rewards.size() + " currency rewards");
		}

		/** Returns whether the reward map is empty */
		public static boolean isEmpty() {
			return MAP.isEmpty();
		}

		/** Returns the associated item */
		public Item getItem() {
			return Registries.ITEM.get(this.item());
		}

	}

	/** A currency skeleton */
	record Skeleton(Identifier item, List<Identifier> price) {

		/** The inner map */
		private static final HashMap<Identifier, Skeleton> MAP = new HashMap<>(0);

		/** Returns the currency skeleton associated with the item */
		public static Optional<Skeleton> find(ItemStack item) {
			return find(item.getItem());
		}

		/** Returns the currency skeleton associated with the item */
		public static Optional<Skeleton> find(Item item) {
			return find(Registries.ITEM.getId(item));
		}

		/** Returns the currency skeleton associated with the item's identifier */
		public static Optional<Skeleton> find(Identifier item) {
			return MAP.values().stream().filter(s -> s.item().equals(item)).findFirst();
		}

		/** Returns a list of skeletons that use the given reward as a component. */
		public static List<Skeleton> findFromPrice(Reward reward) {
			return MAP.values().stream().filter(s -> s.getPrice().contains(reward)).toList();
		}

		/** Returns the list of rewards for this skeleton */
		public List<Reward> getPrice() {
			return this.price()
				.stream()
				.map(id -> Optional.ofNullable(Reward.MAP.get(id)))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
		}

		/** Loads the given currency skeletons into the internal map */
		public static void load(Map<Identifier, Skeleton> skeletons) {
			MAP.clear();
			MAP.putAll(skeletons);
			Astral.LOGGER.info("Loaded " + skeletons.size() + " currency skeletons");
		}

		/** Returns whether the skeleton map is empty */
		public static boolean isEmpty() {
			return MAP.isEmpty();
		}

		/** Returns the associated item */
		public Item getItem() {
			return Registries.ITEM.get(this.item());
		}

		/** Returns whether the given item map contains the price of this skeleton */
		public boolean hasPrice(HashMap<Reward, Integer> items) {
			final List<Reward> price = this.getPrice();
			final HashMap<Reward, Integer> required = new HashMap<>(price.size());

			for (final Reward reward : price) {
				if (required.containsKey(reward)) {
					required.put(reward, required.get(reward) + 1);
				} else {
					required.put(reward, 1);
				}
			}

			return required.entrySet().stream().allMatch(e -> items.get(e.getKey()) >= e.getValue());
		}

	}

	/** Loads currency data on reload */
	final class Loader extends JsonDataLoader implements IdentifiableResourceReloadListener {

		/** This loader's GSON configuration */
		private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
		/** The folder that contains the currency data */
		private static final String FOLDER = "currency";

		public Loader() {
			super(Loader.GSON, Loader.FOLDER);
		}

		@Override
		public Identifier getFabricId() {
			return Astral.getId(Loader.FOLDER);
		}

		@Override
		protected void apply(Map<Identifier, JsonElement> data, ResourceManager _manager, Profiler _profiler) {
			data.entrySet().stream().filter(e -> e.getValue() instanceof JsonObject).forEach(e -> {
				final JsonObject json = (JsonObject) e.getValue();
				final String namespace = e.getKey().getNamespace();
				final String path = e.getKey().getPath();

				switch (path.replaceFirst("\\.json$", "")) {
					case "units" -> this.loadUnits(namespace, json);
					case "rewards" -> this.loadRewards(namespace, json);
					case "skeletons" -> this.loadSkeletons(namespace, json);
				}
			});
		}

		/** Loads currency units from the given JSON object */
		private void loadUnits(String namespace, JsonObject json) {
			final HashMap<Identifier, Unit> map = new HashMap<>(json.entrySet().size());

			json.asMap().forEach((key, data) -> {
				final Identifier id;

				if ((id = Identifier.tryParse(key)) == null) {
					Astral.LOGGER.warn("Invalid identifier: '" + key + "'");
					return;
				}
				if (!(data instanceof JsonObject object)) {
					Astral.LOGGER.warn("Expected an object for '" + key + "'");
					return;
				}

				final int value = JsonHelper.getInt(object, "value", 0);
				final boolean drops = JsonHelper.getBoolean(object, "drops", false);
				final Identifier item = Optional.ofNullable(Identifier.tryParse(JsonHelper.getString(object, "item")))
					.orElseGet(Registries.ITEM::getDefaultId);

				map.put(id, new Unit(item, value, drops));
			});

			Unit.load(map);
		}

		/** Loads currency rewards from the given JSON object */
		private void loadRewards(String namespace, JsonObject json) {
			final HashMap<Identifier, Reward> map = new HashMap<>(json.entrySet().size());

			json.asMap().forEach((key, value) -> {
				final Identifier id;

				if ((id = Identifier.tryParse(key)) == null) {
					Astral.LOGGER.warn("Invalid identifier: '" + key + "'");
					return;
				}
				if (!(value instanceof JsonObject object)) {
					Astral.LOGGER.warn("Expected an object for '" + key + "'");
					return;
				}

				final int weight = JsonHelper.getInt(object, "weight", 1);
				final Identifier item = Optional.ofNullable(Identifier.tryParse(JsonHelper.getString(object, "item")))
					.orElseGet(Registries.ITEM::getDefaultId);

				map.put(id, new Reward(item, weight));
			});

			Reward.load(map);
		}

		/** Loads currency skeletons from the given JSON object */
		private void loadSkeletons(String namespace, JsonObject json) {
			final HashMap<Identifier, Skeleton> map = new HashMap<>(json.entrySet().size());

			json.asMap().forEach((key, value) -> {
				final Identifier id;

				if ((id = Identifier.tryParse(key)) == null) {
					Astral.LOGGER.warn("Invalid identifier: '" + key + "'");
					return;
				}
				if (!(value instanceof JsonObject object)) {
					Astral.LOGGER.warn("Expected an object for '" + key + "'");
					return;
				}
				if (!JsonHelper.hasArray(object, "cost")) {
					Astral.LOGGER.warn("Expected a JSON array for key 'cost'");
					return;
				}

				final ArrayList<Identifier> cost = new ArrayList<>();
				final JsonArray costArray = JsonHelper.getArray(object, "cost");

				for (final JsonElement element : costArray) {
					final String string = element.getAsString();
					final Identifier identifier;

					if ((identifier = Identifier.tryParse(string)) == null) {
						Astral.LOGGER.warn("Invalid reward identifier '" + string + "'");
					} else {
						cost.add(identifier);
					}
				}

				final Identifier item = Optional.ofNullable(Identifier.tryParse(JsonHelper.getString(object, "item")))
					.orElseGet(Registries.ITEM::getDefaultId);

				map.put(id, new Skeleton(item, cost));
			});

			Skeleton.load(map);
		}

	}

}
