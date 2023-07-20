package dev.jaxydog.content.power;

import dev.jaxydog.utility.register.Registerable;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.registry.Registry;

/** Abstract class for implementing conditions */
public abstract class CustomCondition<T> implements Registerable.Main {

	/** The custom condition's inner raw identifier */
	private final String RAW_ID;

	public CustomCondition(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Checks the condition */
	public abstract boolean check(Instance data, T value);

	/** Returns the condition's factory */
	public abstract CustomConditionFactory<T> factory();

	/** Returns the action's registry */
	public abstract Registry<ConditionFactory<T>> registry();

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		this.factory().register(this.registry());
	}

}
