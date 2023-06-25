package dev.jaxydog.content.item.color;

import dev.jaxydog.content.item.CustomBlockItem;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/** An extension of a regular custom block item that provides color support */
public abstract class ColoredBlockItem extends CustomBlockItem implements Registerable.Client {

	public ColoredBlockItem(String rawId, Block block, Settings settings) {
		super(rawId, block, settings);
	}

	/** Returns the color that the item stack should render at the given index */
	public abstract int getColor(ItemStack stack, int index);

	@Override
	public void registerClient() {
		ColorProviderRegistry.ITEM.register(this::getColor, this);
	}

}
