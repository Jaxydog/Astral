package dev.jaxydog.astral.content.effect;

import dev.jaxydog.astral.content.effect.CustomPotion.Recipe;
import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.register.ContentRegistrar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DyeColor;

@SuppressWarnings("unused")
public final class CustomPotions extends ContentRegistrar {

    public static final CustomPotion SINISTER = new CustomPotion(
        "sinister",
        new Recipe(Potions.THICK, Ingredient.ofItems(AstralItems.DYEABLE_AMETHYST_CLUSTERS.getComputed(DyeColor.RED))),
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
