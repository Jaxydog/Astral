package dev.jaxydog.content.power;

import dev.jaxydog.utility.register.Registerable;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.registry.Registry;

/** Abstract class for implementing actions */
public abstract class CustomAction<T> implements Registerable.Main {

	/** The custom action's inner raw identifier */
	private final String RAW_ID;

	public CustomAction(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Executes the action */
	public abstract void execute(Instance data, T value);

	/** Returns the action's factory */
	public abstract CustomActionFactory<T> factory();

	/** Returns the action's registry */
	public abstract Registry<ActionFactory<T>> registry();

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		this.factory().register(this.registry());
	}

}
