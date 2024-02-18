package dev.jaxydog.astral.content.effect;

import dev.jaxydog.astral.register.Registered;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class CustomPotion extends Potion implements Registered.Common {

    private final String RAW_ID;
    private final @Nullable Recipe RECIPE;

    public CustomPotion(String rawId, @Nullable Recipe recipe, StatusEffectInstance... effects) {
        super(effects);

        this.RAW_ID = rawId;
        this.RECIPE = recipe;
    }

    @SuppressWarnings("unused")
    public CustomPotion(String rawId, StatusEffectInstance... effects) {
        this(rawId, null, effects);
    }

    @Override
    public String getRegistryPath() {
        return this.RAW_ID;
    }

    @Override
    public void register() {
        Registry.register(Registries.POTION, this.getRegistryId(), this);

        if (this.RECIPE != null) {
            FabricBrewingRecipeRegistry.registerPotionRecipe(this.RECIPE.base(), this.RECIPE.item(), this);
        }
    }

    public record Recipe(Potion base, Ingredient item) { }

}
