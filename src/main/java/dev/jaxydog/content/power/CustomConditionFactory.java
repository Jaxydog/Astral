package dev.jaxydog.content.power;

import dev.jaxydog.Astral;
import dev.jaxydog.register.Registered;
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
		Registry.register(registry, this.getId(), this);
	}

	@Override
	public Identifier getId() {
		return this.getSerializerId();
	}

	@Override
	public String getIdPath() {
		return this.RAW_ID;
	}

}
