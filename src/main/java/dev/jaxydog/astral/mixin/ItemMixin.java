package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.utility.NbtUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/** Provide an NBT tag to disable enchantment glint */
@Mixin(Item.class)
public abstract class ItemMixin {

    /** The NBT key that corresponds to the modded-in enchantment glint disable tag */
    @Unique
    private static final String SET_GLINT_KEY = "SetGlint";

    @ModifyReturnValue(method = "hasGlint", at = @At("RETURN"))
    private boolean forceGlint(boolean result, @Local(argsOnly = true) ItemStack stack) {
        if (NbtUtil.contains(stack, SET_GLINT_KEY)) {
            return NbtUtil.getBoolean(stack, SET_GLINT_KEY);
        } else {
            return result;
        }
    }

}
