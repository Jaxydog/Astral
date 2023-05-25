package dev.jaxydog.astral.mixin;

import dev.jaxydog.astral.utility.NbtUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Provide an NBT tag to disable enchantment glint */
@Mixin(Item.class)
public abstract class ItemMixin {

	private static final String SET_GLINT_KEY = "SetGlint";

	@Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
	private void hasGlintInject(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (NbtUtil.contains(stack, ItemMixin.SET_GLINT_KEY)) {
			callbackInfo.setReturnValue(NbtUtil.getBoolean(stack, ItemMixin.SET_GLINT_KEY));
		}
	}
}
