package dev.jaxydog.content.power;

import dev.jaxydog.register.Registered;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData.Instance;
import io.github.apace100.calio.data.SerializableDataType;

/** Abstract class for implementing conditions with multiple data types */
public abstract class CustomMetaCondition implements Registered.Common {

	/** The custom meta condition's inner raw identifier */
	private final String RAW_ID;

	public CustomMetaCondition(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Checks the condition */
	public abstract <T> boolean check(Instance data, T value);

	@Override
	public String getIdPath() {
		return this.RAW_ID;
	}

	@Override
	public void register() {
		this.factory(ApoliDataTypes.BIENTITY_CONDITION).register(ApoliRegistries.BIENTITY_CONDITION);
		this.factory(ApoliDataTypes.BLOCK_CONDITION).register(ApoliRegistries.BLOCK_CONDITION);
		this.factory(ApoliDataTypes.ENTITY_CONDITION).register(ApoliRegistries.ENTITY_CONDITION);
		this.factory(ApoliDataTypes.ITEM_CONDITION).register(ApoliRegistries.ITEM_CONDITION);
	}

	/** Returns the condition's factory */
	public abstract <T> CustomConditionFactory<T> factory(SerializableDataType<ConditionFactory<T>.Instance> type);

}
