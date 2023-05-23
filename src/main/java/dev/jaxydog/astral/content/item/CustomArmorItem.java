package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.utility.Functions.QuadFunction;
import dev.jaxydog.astral.utility.Functions.QuintFunction;
import dev.jaxydog.astral.utility.Functions.TriFunction;
import dev.jaxydog.astral.utility.Registerable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/** An extension of a regular armor item that provides additional functionality */
public abstract class CustomArmorItem extends ArmorItem implements Registerable.Main {

	/** The custom armor item's inner raw identifier */
	private final String RAW_ID;

	public CustomArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(material, type, settings);
		this.RAW_ID = rawId;
	}

	/** Returns the total number of texture layers that the armor item expects to have */
	public abstract int getTextureLayers(ItemStack stack);

	/** Returns the total number of texture layers that the armor item expects to have */
	public int getTextureLayers() {
		return this.getTextureLayers(this.getDefaultStack());
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		// This will automatically add item tooltips as long as there's a consecutive listing for each
		// lang file entry starting at 0
		var key = stack.getItem().getTranslationKey(stack) + ".lore_";
		var index = 0;

		while (I18n.hasTranslation(key + index)) {
			tooltip.add(Text.translatable(key + index).formatted(Formatting.GRAY));
			index += 1;
		}

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		Main.super.registerMain(); // this should always be called just in case default functionality is added
		Registry.register(Registries.ITEM, this.getId(), this);
		// This adds the default item stack into the item group; change `getDefaultStack` to change how the item
		// will appear in the player's creative inventory
		ItemGroupEvents.modifyEntriesEvent(CustomItem.GROUP).register(e -> e.add(this.getDefaultStack()));
	}

	/** Wraps an armor set and provides automatic registration */
	public static class SetHelper<T extends CustomArmorItem>
		implements Registerable.Client, Registerable.Main, Registerable.Server {

		/** The set's inner map containing the value for each armor type */
		private final HashMap<Type, T> MAP = new HashMap<>(Type.values().length);

		// let's hope that we don't need more than 4 generic arguments lol
		public SetHelper(ArmorMaterial material, TriFunction<String, ArmorMaterial, Type, T> constructor) {
			for (var type : Type.values()) {
				var id = material.getName() + "_" + type.getName();
				var value = constructor.apply(id, material, type);

				this.MAP.put(type, value);
			}
		}

		public <A> SetHelper(
			ArmorMaterial material,
			A arg1,
			QuadFunction<String, ArmorMaterial, Type, A, T> constructor
		) {
			for (var type : Type.values()) {
				var id = material.getName() + "_" + type.getName();
				var value = constructor.apply(id, material, type, arg1);

				this.MAP.put(type, value);
			}
		}

		public <A, B> SetHelper(
			ArmorMaterial material,
			A arg1,
			B arg2,
			QuintFunction<String, ArmorMaterial, Type, A, B, T> constructor
		) {
			for (var type : Type.values()) {
				var id = material.getName() + "_" + type.getName();
				var value = constructor.apply(id, material, type, arg1, arg2);

				this.MAP.put(type, value);
			}
		}

		/** Returns the set's armor item for the given armor type */
		public Optional<T> get(Type type) {
			return Optional.ofNullable(this.MAP.get(type));
		}

		/** This always will return `null` and should not be used */
		@Override
		public String getRawId() {
			return null;
		}

		@Override
		public void registerClient() {
			for (var value : this.MAP.values()) {
				if (!(value instanceof Registerable.Client)) continue;

				((Registerable.Client) value).registerClient();
			}
		}

		@Override
		public void registerMain() {
			for (var value : this.MAP.values()) {
				if (!(value instanceof Registerable.Main)) continue;

				((Registerable.Main) value).registerMain();
			}
		}

		@Override
		public void registerServer() {
			for (var value : this.MAP.values()) {
				if (!(value instanceof Registerable.Server)) continue;

				((Registerable.Server) value).registerServer();
			}
		}
	}
}
