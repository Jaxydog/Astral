package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.jaxydog.utility.CurrencyUtil;
import dev.jaxydog.utility.CurrencyUtil.Reward;
import dev.jaxydog.utility.CurrencyUtil.Unit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin {

	@ModifyReturnValue(
		method = "craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;",
		at = @At("RETURN")
	)
	private ItemStack preventExchange(ItemStack stack) {
		final Item item = stack.getItem();

		if (Unit.find(item).isPresent() || Reward.find(item).isPresent()) {
			stack.getOrCreateNbt().putBoolean(CurrencyUtil.EXCHANGE_KEY, false);
		}

		return stack;
	}

}
