package dev.jaxydog.astral_additions.content.item;

import dev.jaxydog.astral_additions.AstralAdditions;
import dev.jaxydog.astral_additions.utility.Registerable;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/** An extension of a regular item that provides additional functionality */
public class CustomItem extends Item implements Registerable.Main {

	/** The default item group; any registered subclasses should be added automatically */
	public static final ItemGroup GROUP = FabricItemGroup
		.builder(AstralAdditions.getId("default"))
		.icon(() -> Items.ENDER_EYE.getDefaultStack())
		.build();

	/** The custom item's inner raw identifier */
	private final String RAW_ID;

	public CustomItem(String rawId, Settings settings) {
		super(settings);
		this.RAW_ID = rawId;
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
}
