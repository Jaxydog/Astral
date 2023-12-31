package dev.jaxydog.content.item.color;

import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemStack;

/** An extension of a regular custom item that provides color support */
public abstract class ColoredItem extends CustomItem implements Registered.Client {

	public ColoredItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public void registerClient() {
		ColorProviderRegistry.ITEM.register(this::getColor, this);
	}

	/** Returns the color that the item stack should render as at the given index */
	public abstract int getColor(ItemStack stack, int index);

}
