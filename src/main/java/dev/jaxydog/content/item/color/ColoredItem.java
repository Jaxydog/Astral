package dev.jaxydog.content.item.color;

import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemStack;

/** An extension of a regular custom item that provides color support */
public abstract class ColoredItem extends CustomItem implements Registerable.Client {

	public ColoredItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the color that the item stack should render as at the given index */
	public abstract int getColor(ItemStack stack, int index);

	@Override
	public void registerClient() {
		ColorProviderRegistry.ITEM.register(this::getColor, this);
	}

}
