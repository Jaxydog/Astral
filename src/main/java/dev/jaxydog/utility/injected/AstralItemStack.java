package dev.jaxydog.utility.injected;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public interface AstralItemStack {

    void astral$setItem(Item item);

    ItemStack astral$copyWithItemStack(Item item);

}
