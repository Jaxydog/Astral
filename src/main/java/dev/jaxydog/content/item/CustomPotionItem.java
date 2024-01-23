package dev.jaxydog.content.item;

import dev.jaxydog.register.Registered;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class CustomPotionItem extends PotionItem implements Registered.Common {

	private final String RAW_ID;

	public CustomPotionItem(String rawId, Settings settings) {
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
	public String getIdPath() {
		return this.RAW_ID;
	}

	@Override
	public void register() {
		Registry.register(Registries.ITEM, this.getId(), this);
		BrewingRecipeRegistry.registerPotionType(this);
	}

}
