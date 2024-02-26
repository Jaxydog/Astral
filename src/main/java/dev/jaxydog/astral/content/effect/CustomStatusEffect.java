package dev.jaxydog.astral.content.effect;

import dev.jaxydog.astral.register.Registered;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CustomStatusEffect extends StatusEffect implements Registered.Common {

    private final String RAW_ID;

    public CustomStatusEffect(String rawId, StatusEffectCategory category, int color) {
        super(category, color);

        this.RAW_ID = rawId;
    }

    @Override
    public String getRegistryIdPath() {
        return this.RAW_ID;
    }

    @Override
    public void register() {
        Registry.register(Registries.STATUS_EFFECT, this.getRegistryId(), this);
    }

}
