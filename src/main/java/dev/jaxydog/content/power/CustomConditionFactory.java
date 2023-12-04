package dev.jaxydog.content.power;

import java.util.function.BiFunction;
import dev.jaxydog.Astral;
import dev.jaxydog.utility.register.Registerable;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** An extension of a regular condition factory that provides additional functionality */
public class CustomConditionFactory<T> extends ConditionFactory<T> implements Registerable {

	/** The custom condition factory's inner raw identifier */
	private final String RAW_ID;

	public CustomConditionFactory(String rawId, SerializableData data,
			BiFunction<SerializableData.Instance, T, Boolean> condition) {
		super(Astral.getId(rawId), data, condition);

		this.RAW_ID = rawId;
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public Identifier getId() {
		return this.getSerializerId();
	}

	/** Registers the factory in the given registry */
	public void register(Registry<ConditionFactory<T>> registry) {
		Registry.register(registry, this.getId(), this);
	}

}
