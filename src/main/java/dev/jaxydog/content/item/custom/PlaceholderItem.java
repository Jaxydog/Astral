package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.content.item.Customized;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.HashMap;

public class PlaceholderItem extends CustomItem implements Customized, Equipment {

	private static final HashMap<Integer, String> TRANSLATION_KEYS = new HashMap<>();

	public PlaceholderItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		final int data = this.getCustomModelData(stack);

		if (data == 0) return super.getTranslationKey(stack);

		if (!TRANSLATION_KEYS.containsKey(data)) {
			TRANSLATION_KEYS.put(data, super.getTranslationKey(stack) + "." + data);
		}

		return TRANSLATION_KEYS.get(data);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public void registerMain() {
		Registry.register(Registries.ITEM, this.getId(), this);
	}

}
