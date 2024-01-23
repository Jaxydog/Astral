package dev.jaxydog.content.power;

import dev.jaxydog.register.Registered;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.registry.Registry;

/** Abstract class for implementing conditions */
public abstract class CustomCondition<T> implements Registered.Common {

	/** The custom condition's inner raw identifier */
	private final String RAW_ID;

	public CustomCondition(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Checks the condition */
	public abstract boolean check(Instance data, T value);

	@Override
	public String getRegistryIdPath() {
		return this.RAW_ID;
	}

	@Override
	public void register() {
		this.factory().register(this.registry());
	}

	/** Returns the condition's factory */
	public abstract CustomConditionFactory<T> factory();

	/** Returns the action's registry */
	@SuppressWarnings("SameReturnValue")
	public abstract Registry<ConditionFactory<T>> registry();

}
