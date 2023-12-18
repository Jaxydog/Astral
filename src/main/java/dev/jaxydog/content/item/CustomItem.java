package dev.jaxydog.content.item;

import dev.jaxydog.Astral;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/** An extension of a regular item that provides additional functionality */
public class CustomItem extends Item implements Registerable.Main {

	/** The custom item's inner raw identifier */
	private final String RAW_ID;

	public CustomItem(String rawId, Settings settings) {
		super(settings);

		this.RAW_ID = rawId;
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		final String key = stack.getItem().getTranslationKey(stack) + ".lore_";
		int index = 0;

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

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Override
	public void registerMain() {
		Registry.register(Registries.ITEM, this.getId(), this);
		ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(Astral.ITEM_GROUP).get())
			.register(g -> g.add(this));
	}

}
