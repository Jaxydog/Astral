package dev.jaxydog;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.lang.reflect.Modifier.*;

/** The mod data generator entrypoint */
public final class AstralDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		final FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(Advancements::new);
		LootTables.addAllProviders(pack);
		pack.addProvider(Models::new);
		Tags.addAllProviders(pack);
		pack.addProvider(Recipes::new);
	}

	public static class Advancements extends FabricAdvancementProvider {

		private static @Nullable Advancements instance;

		private final HashMap<Identifier, Advancement.Builder> advancements = new HashMap<>();

		public Advancements(FabricDataOutput output) {
			super(output);

			if (instance != null) throw new RuntimeException("Only one instance may be constructed");

			instance = this;
		}

		public static @NotNull Advancements getInstance() {
			if (instance == null) throw new RuntimeException("An instance has not been constructed");

			return instance;
		}

		public void generate(Identifier id, Advancement.Builder builder) {
			this.advancements.put(id, builder);
		}

		@Override
		public void generateAdvancement(Consumer<Advancement> consumer) {
			this.advancements.forEach((id, builder) -> builder.build(consumer, id.toString()));
		}

	}

	public static class LootTables extends SimpleFabricLootTableProvider {

		private static final HashMap<LootContextType, LootTables> instances = new HashMap<>();

		private final HashMap<Identifier, LootTable.Builder> tables = new HashMap<>();

		public LootTables(FabricDataOutput output, LootContextType lootContextType) {
			super(output, lootContextType);

			if (instances.containsKey(lootContextType)) {
				throw new RuntimeException("Only one instance may be constructed");
			} else {
				instances.put(lootContextType, this);
			}
		}

		public static void addAllProviders(FabricDataGenerator.Pack pack) {
			for (final Field field : LootContextTypes.class.getFields()) {
				final int modifiers = field.getModifiers();

				if (!(isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers))) continue;

				try {
					final Object value = field.get(null);

					if (value instanceof final LootContextType type) {
						pack.addProvider((Pack.Factory<LootTables>) output -> new LootTables(output, type) {
							@Override
							public void generate(Identifier id, Builder builder) {
								super.generate(id, builder.type(type));
							}
						});
					}
				} catch (IllegalAccessException exception) {
					Astral.LOGGER.error(exception.toString());
				}
			}
		}

		public static @NotNull LootTables getInstance(LootContextType lootContextType) {
			final LootTables instance = instances.get(lootContextType);

			if (instance == null) throw new RuntimeException("An instance has not been constructed");

			return instance;
		}

		public void generate(Identifier id, LootTable.Builder builder) {
			this.tables.put(id, builder);
		}

		@Override
		public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
			this.tables.forEach(exporter);
		}

	}

	public static class Models extends FabricModelProvider {

		private static @Nullable Models instance = null;

		private final ArrayList<Consumer<BlockStateModelGenerator>> blocks = new ArrayList<>();
		private final ArrayList<Consumer<ItemModelGenerator>> items = new ArrayList<>();

		public Models(FabricDataOutput output) {
			super(output);

			if (instance != null) throw new RuntimeException("Only one instance may be constructed");

			instance = this;
		}

		public static @NotNull Models getInstance() {
			if (instance == null) throw new RuntimeException("An instance has not been constructed");

			return instance;
		}

		public void generateBlock(Consumer<BlockStateModelGenerator> consumer) {
			this.blocks.add(consumer);
		}

		public void generateItem(Consumer<ItemModelGenerator> consumer) {
			this.items.add(consumer);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
			this.blocks.forEach(c -> c.accept(blockStateModelGenerator));
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			this.items.forEach(c -> c.accept(itemModelGenerator));
		}

	}

	public static class Tags<T> extends FabricTagProvider<T> {

		private static final HashMap<RegistryKey<? extends Registry<?>>, Tags<?>> instances = new HashMap<>();

		private final ArrayList<BiConsumer<Tags<T>, WrapperLookup>> tags = new ArrayList<>();

		public Tags(
			FabricDataOutput output,
			RegistryKey<? extends Registry<T>> registryKey,
			CompletableFuture<WrapperLookup> registriesFuture
		) {
			super(output, registryKey, registriesFuture);

			if (instances.containsKey(registryKey)) {
				throw new RuntimeException("Only one instance may be constructed");
			} else {
				instances.put(registryKey, this);
			}
		}

		public static void addAllProviders(FabricDataGenerator.Pack pack) {
			for (final Field field : Registries.class.getFields()) {
				final int modifiers = field.getModifiers();

				if (!(isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers))) continue;

				try {
					final Object value = field.get(null);

					if (value instanceof final Registry<?> registry && !registry.getKey()
						.getValue()
						.equals(Registries.ROOT_KEY)) {
						pack.addProvider((output, future) -> new Tags<>(output, registry.getKey(), future));
					}
				} catch (IllegalAccessException exception) {
					Astral.LOGGER.error(exception.toString());
				}
			}
		}

		@SuppressWarnings("unchecked") // these are managed internally and should always be valid casts.
		public static <T> Tags<T> getInstance(RegistryKey<? extends Registry<T>> registryKey) {
			final Tags<?> instance = instances.get(registryKey);

			if (instance == null) throw new RuntimeException("An instance has not been constructed");

			return (Tags<T>) instance;
		}

		public void generate(BiConsumer<Tags<T>, WrapperLookup> consumer) {
			this.tags.add(consumer);
		}

		@Override
		protected void configure(WrapperLookup lookup) {
			this.tags.forEach(c -> c.accept(this, lookup));
		}

	}

	public static class Recipes extends FabricRecipeProvider {

		private static @Nullable Recipes instance;

		private final HashMap<Identifier, CraftingRecipeJsonBuilder> recipes = new HashMap<>();

		public Recipes(FabricDataOutput output) {
			super(output);

			if (instance != null) throw new RuntimeException("Only one instance may be constructed");

			instance = this;
		}

		public static @NotNull Recipes getInstance() {
			if (instance == null) throw new RuntimeException("An instance has not been constructed");

			return instance;
		}

		public void generate(Identifier id, CraftingRecipeJsonBuilder builder) {
			this.recipes.put(id, builder);
		}

		@Override
		public void generate(Consumer<RecipeJsonProvider> exporter) {
			this.recipes.forEach((id, builder) -> builder.offerTo(exporter, id));
		}

	}

}
