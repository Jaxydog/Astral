package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.utility.Registerable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

/** An extension of a regular custom armor item that provides color support */
public abstract class ColoredArmorItem extends CustomArmorItem implements Registerable.Client {

	public ColoredArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(rawId, material, type, settings);
	}

	/** Returns the color that the item stack should render at the given index */
	public abstract int getColor(ItemStack stack, int index);

	@Override
	public void registerClient() {
		Client.super.registerClient();
		// This just ensures that registered items are actually colored
		// Calling this on the server will crash it, as the `ColorProviderRegistry` doesn't exist on that environment
		ColorProviderRegistry.ITEM.register(this::getColor, this);
	}
}
