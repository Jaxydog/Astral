package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.utility.register.Registerable;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;

/** Provides an interface for easily creating meta actions */
public abstract class MetaAction implements Registerable.Main {

	/** The meta action's inner raw identifier */
	private final String RAW_ID;

	public MetaAction(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Executes the meta action */
	public abstract <T> void action(SerializableData.Instance data, T t);

	/** Provides an action factory that will be registered by the meta action */
	public abstract <T> CustomActionFactory<T> getFactory(
		SerializableDataType<CustomActionFactory<T>.Instance> data,
		Supplier<Registry<ActionFactory<T>>> registry
	);

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();

		this.getFactory(ApoliDataTypes.BIENTITY_ACTION, () -> ApoliRegistries.BIENTITY_ACTION).registerMain();
		this.getFactory(ApoliDataTypes.BLOCK_ACTION, () -> ApoliRegistries.BLOCK_ACTION).registerMain();
		this.getFactory(ApoliDataTypes.ENTITY_ACTION, () -> ApoliRegistries.ENTITY_ACTION).registerMain();
		this.getFactory(ApoliDataTypes.ITEM_ACTION, () -> ApoliRegistries.ITEM_ACTION).registerMain();
	}
}
