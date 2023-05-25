package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.register.Registerable;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** An extension of a regular condition factory that provides additional functionality */
public class CustomConditionFactory<T> extends ConditionFactory<T> implements Registerable.Main {

	/** The custom condition factory's inner raw identifier */
	private final String RAW_ID;
	/** Function that supplies the condition factory's registry instance */
	private final Supplier<Registry<ConditionFactory<T>>> REGISTRY_SUPPLIER;

	public CustomConditionFactory(
		String rawId,
		SerializableData data,
		BiFunction<SerializableData.Instance, T, Boolean> condition,
		Supplier<Registry<ConditionFactory<T>>> registry
	) {
		super(Astral.getId(rawId), data, condition);
		this.RAW_ID = rawId;
		this.REGISTRY_SUPPLIER = registry;
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public Identifier getId() {
		return this.getSerializerId();
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();
		Registry.register(this.REGISTRY_SUPPLIER.get(), this.getId(), this);
	}
}
