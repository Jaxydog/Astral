package dev.jaxydog.content.effect;

import dev.jaxydog.content.effect.CustomPotion.Recipe;
import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.register.ContentRegistrar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DyeColor;

@SuppressWarnings("unused")
public final class CustomPotions extends ContentRegistrar {

    public static final CustomPotion SINISTER = new CustomPotion(
        "sinister",
        new Recipe(Potions.THICK, Ingredient.ofItems(CustomItems.DYEABLE_AMETHYST_CLUSTERS.get(DyeColor.RED))),
        new StatusEffectInstance(CustomStatusEffects.SINISTER, 15 * 20, 0)
    );

    public static final CustomPotion LONG_SINISTER = new CustomPotion(
        "long_sinister",
        new Recipe(CustomPotions.SINISTER, Ingredient.ofItems(Items.REDSTONE)),
        new StatusEffectInstance(CustomStatusEffects.SINISTER, 30 * 20, 0)
    );

    public static final CustomPotion STRONG_SINISTER = new CustomPotion(
        "strong_sinister",
        new Recipe(CustomPotions.SINISTER, Ingredient.ofItems(Items.GLOWSTONE_DUST)),
        new StatusEffectInstance(CustomStatusEffects.SINISTER, 10 * 20, 1)
    );

}
