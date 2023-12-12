package dev.jaxydog.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import dev.jaxydog.utility.AstralItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AstralItemStack {

	@Shadow
	@Final
	@Mutable
	private Item item;

	@Override
	public void astral$setItem(Item item) {
		this.item = item;
	}

}
