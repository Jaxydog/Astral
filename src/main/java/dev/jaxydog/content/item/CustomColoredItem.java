package dev.jaxydog.content.item;

import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemStack;

/** An extension of a regular custom item that provides color support */
public abstract class CustomColoredItem extends CustomItem implements Registered.Client {

    public CustomColoredItem(String rawId, Settings settings) {
        super(rawId, settings);
    }

    /** Returns the color that the item stack should render as at the given index */
    public abstract int getColor(ItemStack stack, int index);

    @Override
    public void registerClient() {
        ColorProviderRegistry.ITEM.register(this::getColor, this);
    }

}
