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

package dev.jaxydog.astral.content.item;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An extension of an {@link ArmorItem} that provides commonly used functionality.
 * <p>
 * This is one of the various instances of already-provided wrapper classes for commonly used types.
 * <p>
 * In future code, you should prefer to extend this class over {@link ArmorItem} if at all possible.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 * @see Custom
 */
public class AstralArmorItem extends ArmorItem implements Custom, LoreHolder {

	/** The item's identifier path used within the registration system. */
	private final String path;
	/** The item's preferred item group, or {@code null} if it should not be added to any group. */
	private final @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup;

	/**
	 * Creates a new armor item using the given settings.
	 * <p>
	 * If the {@link #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
	 *
	 * @param path The item's identifier path.
	 * @param material The armor's material.
	 * @param type The armor's type.
	 * @param settings The item's settings.
	 * @param preferredGroup The item's preferred item group.
	 */
	public AstralArmorItem(
		String path,
		ArmorMaterial material,
		Type type,
		Settings settings,
		@Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup
	) {
		super(material, type, settings);

		this.path = path;
		this.preferredGroup = preferredGroup;
	}

	/**
	 * Creates a new armor item using the given settings.
	 * <p>
	 * This item will not be added to any item groups.
	 *
	 * @param path The item's identifier path.
	 * @param material The armor's material.
	 * @param type The armor's type.
	 * @param settings The item's settings.
	 */
	public AstralArmorItem(String path, ArmorMaterial material, Type type, Settings settings) {
		this(path, material, type, settings, null);
	}

	/**
	 * Returns the total number of armor texture layers that this item stack expects to render.
	 * <p>
	 * This method allows for armor items to have multiple texture layers per item.
	 *
	 * @param stack The item stack.
	 *
	 * @return The number of texture layers.
	 */
	@SuppressWarnings("unused")
	public int getArmorTextureLayers(ItemStack stack) {
		return 1;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.addAll(this.getLoreTooltips(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public Optional<RegistryKey<ItemGroup>> getItemGroup() {
		return Optional.ofNullable(this.preferredGroup).map(Supplier::get);
	}

	@Override
	public String getRegistryPath() {
		return this.path;
	}

	/**
	 * Implements the {@link ArmorMaterial} interface as a buildable class.
	 *
	 * @author Jaxydog
	 */
	public static class Material implements ArmorMaterial {

		/** The armor material's durability values. */
		private final Map<Type, Integer> durability = new Object2IntOpenHashMap<>(Type.values().length);
		/** The armor material's protection values. */
		private final Map<Type, Integer> protection = new Object2IntOpenHashMap<>(Type.values().length);
		/** The armor material's enchantability. */
		private final int enchantability;
		/** The armor material's equipping sound event. */
		private final SoundEvent equipSound;
		/** The armor material's repair ingredient. */
		private final Ingredient repairIngredient;
		/** The armor material's name. */
		private final String name;
		/** The armor material's toughness. */
		private final float toughness;
		/** The armor material's knockback resistance. */
		private final float knockbackResistance;

		/**
		 * Creates a new armor material.
		 *
		 * @param durability The armor's durability.
		 * @param protection The armor's protection.
		 * @param enchantability The armor's enchantability.
		 * @param equipSound The armor's equipping sound.
		 * @param repairIngredient The armor's repair ingredient.
		 * @param name The armor's name.
		 * @param toughness The armor's toughness.
		 * @param knockbackResistance The armor's knockback resistance.
		 */
		protected Material(
			Map<Type, Integer> durability,
			Map<Type, Integer> protection,
			int enchantability,
			SoundEvent equipSound,
			Ingredient repairIngredient,
			String name,
			float toughness,
			float knockbackResistance
		) {
			this.durability.putAll(durability);
			this.protection.putAll(protection);
			this.enchantability = enchantability;
			this.equipSound = equipSound;
			this.repairIngredient = repairIngredient;
			this.name = name;
			this.toughness = toughness;
			this.knockbackResistance = knockbackResistance;
		}

		/**
		 * Returns a new builder for an {@link Material}.
		 *
		 * @param path The identifier path.
		 *
		 * @return A new builder.
		 */
		@Contract("_ -> new")
		public static @NotNull Builder builder(String path) {
			return new Builder(path);
		}

		@Override
		public int getDurability(Type type) {
			return Math.max(1, this.durability.get(type));
		}

		@Override
		public int getProtection(Type type) {
			return Math.max(0, this.protection.get(type));
		}

		@Override
		public int getEnchantability() {
			return Math.max(0, this.enchantability);
		}

		@Override
		public SoundEvent getEquipSound() {
			return this.equipSound;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return this.repairIngredient;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public float getToughness() {
			return this.toughness;
		}

		@Override
		public float getKnockbackResistance() {
			return this.knockbackResistance;
		}

		/**
		 * Builds and constructs an instance of a new {@link Material}.
		 *
		 * @author Jaxydog
		 */
		@SuppressWarnings("unused")
		public static class Builder {

			/** The armor material's durability values. */
			private final Map<Type, Integer> durability = new Object2IntOpenHashMap<>(Type.values().length);
			/** The armor material's protection values. */
			private final Map<Type, Integer> protection = new Object2IntOpenHashMap<>(Type.values().length);
			/** The armor material's name. */
			private final String name;

			/** The armor material's enchantability. */
			private int enchantability = 0;
			/** The armor material's equipping sound event. */
			private SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
			/** The armor material's repair ingredient. */
			private Ingredient repairIngredient = Ingredient.empty();
			/** The armor material's toughness. */
			private float toughness = 0F;
			/** The armor material's knockback resistance. */
			private float knockbackResistance = 0F;

			/**
			 * Creates a new armor material builder.
			 * <p>
			 * This is only accessible through {@link #builder(String)} or subclasses.
			 *
			 * @param name The armor's name.
			 */
			protected Builder(String name) {
				this.name = name;
			}

			/**
			 * Sets the durability of the specified armor type.
			 *
			 * @param type The armor type.
			 * @param durability The expected durability.
			 *
			 * @return The builder instance.
			 */
			public Builder setDurability(Type type, int durability) {
				this.durability.put(type, Math.max(1, durability));

				return this;
			}

			/**
			 * Sets the durability of the specified armor type.
			 *
			 * @param helmet The helmet's durability.
			 * @param chestplate The chestplate's durability.
			 * @param leggings The leggings' durability.
			 * @param boots The boots' durability.
			 *
			 * @return The builder instance.
			 */
			public Builder setDurability(int helmet, int chestplate, int leggings, int boots) {
				return this.setDurability(Type.HELMET, helmet)
					.setDurability(Type.CHESTPLATE, chestplate)
					.setDurability(Type.LEGGINGS, leggings)
					.setDurability(Type.BOOTS, boots);
			}

			/**
			 * Sets the protection level of the specified armor type.
			 *
			 * @param type The armor type.
			 * @param protection The expected protection level.
			 *
			 * @return The builder instance.
			 */
			public Builder setProtection(Type type, int protection) {
				this.protection.put(type, protection);

				return this;
			}

			/**
			 * Sets the protection level of the specified armor type.
			 *
			 * @param helmet The helmet's protection level.
			 * @param chestplate The chestplate's protection level.
			 * @param leggings The leggings' protection level.
			 * @param boots The boots' protection level.
			 *
			 * @return The builder instance.
			 */
			public Builder setProtection(int helmet, int chestplate, int leggings, int boots) {
				return this.setProtection(Type.HELMET, helmet)
					.setProtection(Type.CHESTPLATE, chestplate)
					.setProtection(Type.LEGGINGS, leggings)
					.setProtection(Type.BOOTS, boots);
			}

			/**
			 * Sets the enchantability of the armor set.
			 * <p>
			 * This controls the average enchantments that this armor may receive.
			 *
			 * @param enchantability The armor's enchantability level.
			 *
			 * @return The builder instance.
			 */
			public Builder setEnchantability(int enchantability) {
				this.enchantability = enchantability;

				return this;
			}

			/**
			 * Sets the armor set's equip sound.
			 *
			 * @param equipSound The sound to play when equipping the armor.
			 *
			 * @return The builder instance.
			 */
			public Builder setEquipSound(SoundEvent equipSound) {
				this.equipSound = equipSound;

				return this;
			}

			/**
			 * Sets the armor's repair ingredient.
			 *
			 * @param repairIngredient The ingredient that may repair an armor item.
			 *
			 * @return The builder instance.
			 */
			public Builder setRepairIngredient(Ingredient repairIngredient) {
				this.repairIngredient = repairIngredient;

				return this;
			}

			/**
			 * Sets the armor's toughness.
			 * <p>
			 * Toughness effectively removes damage taken when the armor is equipped.
			 *
			 * @param toughness The armor's toughness.
			 *
			 * @return The builder instance.
			 */
			public Builder setToughness(float toughness) {
				this.toughness = toughness;

				return this;
			}

			/**
			 * Sets the armor's knockback resistance.
			 * <p>
			 * Toughness effectively removes knockback taken when the armor is equipped.
			 *
			 * @param knockbackResistance The armor's knockback resistance.
			 *
			 * @return The builder instance.
			 */
			public Builder setKnockbackResistance(float knockbackResistance) {
				this.knockbackResistance = knockbackResistance;

				return this;
			}

			/**
			 * Builds the material instance.
			 *
			 * @return A new material.
			 */
			public Material build() {
				return new Material(
					this.durability,
					this.protection,
					this.enchantability,
					this.equipSound,
					this.repairIngredient,
					this.name,
					this.toughness,
					this.knockbackResistance
				);
			}

		}

	}

}
