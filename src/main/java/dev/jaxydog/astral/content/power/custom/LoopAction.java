package dev.jaxydog.astral.content.power.custom;

import dev.jaxydog.astral.content.power.CustomActionFactory;
import dev.jaxydog.astral.content.power.MetaAction;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableData.Instance;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;

public class LoopAction extends MetaAction {

	public LoopAction(String rawId) {
		super(rawId);
	}

	@Override
	public <T> void action(Instance data, T value) {
		var action = data.<CustomActionFactory<T>.Instance>get("action");

		for (var i = 0; i < data.getInt("repeat"); i += 1) {
			action.accept(value);
		}
	}

	@Override
	public <T> CustomActionFactory<T> getFactory(
		SerializableDataType<ActionFactory<T>.Instance> data,
		Supplier<Registry<ActionFactory<T>>> registry
	) {
		return new CustomActionFactory<>(
			this.getRawId(),
			new SerializableData().add("repeat", SerializableDataTypes.INT, 1).add("action", data),
			this::action,
			registry
		);
	}
}
