package dev.jaxydog.mixin;

import dev.jaxydog.utility.injected.AstralItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

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

    @SuppressWarnings("RedundantCast")
    @Override
    public ItemStack astral$copyWithItemStack(Item item) {
        if (((ItemStack) (Object) this).isEmpty()) {
            return ItemStack.EMPTY;
        }

        final ItemStack copy = ((ItemStack) (Object) this).copy();

        ((AstralItemStack) copy).astral$setItem(item);

        return copy;
    }

}
