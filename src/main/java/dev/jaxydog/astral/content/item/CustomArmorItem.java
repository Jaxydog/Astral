package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.utility.Functions.QuadFunction;
import dev.jaxydog.astral.utility.Registerable;
import java.util.ArrayList;
import java.util.List;
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

	public static <T extends CustomArmorItem> List<T> createSet(
		QuadFunction<String, ArmorMaterial, Type, Settings, T> constructor,
		ArmorMaterial material,
		Settings settings
	) {
		var base = material.getName() + "_";
		var list = new ArrayList<T>(4);

		for (var type : Type.values()) {
			var name = base + type.getName();
			var item = constructor.apply(name, material, type, settings);

			list.add(item);
		}

		return list;
	}

	/** Returns the total number of texture layers that the armor item expects to have */
	public abstract int getTextureLayers();

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
}
