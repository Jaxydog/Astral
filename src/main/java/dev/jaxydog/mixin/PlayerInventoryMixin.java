package dev.jaxydog.mixin;

import dev.jaxydog.utility.CurrencyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

	@Shadow
	@Final
	public PlayerEntity player;

	@Shadow
	@Final
	private List<DefaultedList<ItemStack>> combinedInventory;

	@Shadow
	public abstract int size();

	@Shadow
	public abstract ItemStack getStack(int slot);

	@Shadow
	public abstract int remove(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory);

	@Inject(method = "updateItems", at = @At("TAIL"))
	private void updateItemsInject(CallbackInfo callbackInfo) {
		CurrencyUtil.tryExchange(this.player, (PlayerInventory) (Object) this);
	}

}
