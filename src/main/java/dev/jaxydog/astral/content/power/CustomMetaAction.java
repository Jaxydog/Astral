package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.register.Registered;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData.Instance;
import io.github.apace100.calio.data.SerializableDataType;

/** Abstract class for implementing actions with multiple data types */
public abstract class CustomMetaAction implements Registered.Common {

	/** The custom meta action's inner identifier */
	private final String RAW_ID;

	public CustomMetaAction(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Executes the action */
	public abstract <T> void execute(Instance data, T value);

	@Override
	public String getRegistryIdPath() {
		return this.RAW_ID;
	}

	@Override
	public void register() {
		this.factory(ApoliDataTypes.BIENTITY_ACTION).register(ApoliRegistries.BIENTITY_ACTION);
		this.factory(ApoliDataTypes.BLOCK_ACTION).register(ApoliRegistries.BLOCK_ACTION);
		this.factory(ApoliDataTypes.ENTITY_ACTION).register(ApoliRegistries.ENTITY_ACTION);
		this.factory(ApoliDataTypes.ITEM_ACTION).register(ApoliRegistries.ITEM_ACTION);
	}

	/** Returns the action's factory */
	public abstract <T> CustomActionFactory<T> factory(SerializableDataType<ActionFactory<T>.Instance> type);

}
