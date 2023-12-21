package dev.jaxydog.mixin;

import dev.jaxydog.utility.CurrencyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {

	@Shadow
	@Final
	public PlayerEntity player;

	@Inject(method = "updateItems", at = @At("TAIL"))
	private void updateItemsInject(CallbackInfo callbackInfo) {
		CurrencyUtil.tryExchange(this.player, (PlayerInventory) (Object) this);
	}

}
