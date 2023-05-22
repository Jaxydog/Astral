package dev.jaxydog.astral.content.effect;

import dev.jaxydog.astral.utility.Registerable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** An extension of a regular status effect that provides additional functionality */
public class CustomStatusEffect extends StatusEffect implements Registerable.Main {

	/** The custom status effect's inner raw identifier */
	private final String RAW_ID;

	public CustomStatusEffect(String rawId, StatusEffectCategory category, int color) {
		super(category, color);
		this.RAW_ID = rawId;
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();
		Registry.register(Registries.STATUS_EFFECT, this.getId(), this);
	}
}
