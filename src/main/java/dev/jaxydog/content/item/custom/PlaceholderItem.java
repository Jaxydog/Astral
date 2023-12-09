package dev.jaxydog.content.item.custom;

import java.util.HashMap;
import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.content.item.Customized;
import net.minecraft.item.ItemStack;

public class PlaceholderItem extends CustomItem implements Customized {

	private static final HashMap<Integer, String> TRANSLATION_KEYS = new HashMap<>();

	public PlaceholderItem(String rawId, Settings settings) {
		super(rawId, settings);
	}


	@Override
	public String getTranslationKey(ItemStack stack) {
		final int data = this.getCustomModelData(stack);

		if (data == 0) {
			return super.getTranslationKey(stack);
		}

		if (PlaceholderItem.TRANSLATION_KEYS.containsKey(data)) {
			return PlaceholderItem.TRANSLATION_KEYS.get(data);
		} else {
			final String key = super.getTranslationKey(stack) + "." + data;

			PlaceholderItem.TRANSLATION_KEYS.put(data, key);

			return key;
		}
	}

}
