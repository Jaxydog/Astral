package dev.jaxydog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.jaxydog.utility.CurrencyUtil;
import dev.jaxydog.utility.CurrencyUtil.Reward;
import dev.jaxydog.utility.CurrencyUtil.Unit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapelessRecipe;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin {

	@Inject(method = "craft", at = @At("RETURN"), cancellable = true)
	private void craftInject(CallbackInfoReturnable<ItemStack> callbackInfo) {
		final ItemStack stack = callbackInfo.getReturnValue();
		final Item item = stack.getItem();

		if (!(Unit.find(item).isPresent() || Reward.find(item).isPresent())) {
			return;
		}

		stack.getOrCreateNbt().putBoolean(CurrencyUtil.EXCHANGE_KEY, false);

		callbackInfo.setReturnValue(stack);
	}

}
