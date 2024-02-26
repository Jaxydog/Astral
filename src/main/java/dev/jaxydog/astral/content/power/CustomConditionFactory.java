package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.register.Registered;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

/** An extension of a regular condition factory that provides additional functionality */
public class CustomConditionFactory<T> extends ConditionFactory<T> implements Registered {

	/** The custom condition factory's inner raw identifier */
	private final String RAW_ID;

	public CustomConditionFactory(
		String rawId, SerializableData data, BiFunction<SerializableData.Instance, T, Boolean> condition
	) {
		super(Astral.getId(rawId), data, condition);

		this.RAW_ID = rawId;
	}

	/** Registers the factory in the given registry */
	public void register(Registry<ConditionFactory<T>> registry) {
		Registry.register(registry, this.getRegistryId(), this);
	}

	@Override
	public Identifier getRegistryId() {
		return this.getSerializerId();
	}

	@Override
	public String getRegistryIdPath() {
		return this.RAW_ID;
	}

}
