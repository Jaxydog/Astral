package dev.jaxydog.content.item.color;

import dev.jaxydog.content.item.CustomArmorItem;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

/** An extension of a regular custom armor item that provides color support */
public abstract class ColoredArmorItem extends CustomArmorItem implements Registerable.Client {

	public ColoredArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(rawId, material, type, settings);
	}

	@Override
	public void registerClient() {
		ColorProviderRegistry.ITEM.register(this::getColor, this);
	}

	/** Returns the color that the item stack should render at the given index */
	public abstract int getColor(ItemStack stack, int index);

}
