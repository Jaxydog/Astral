package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomActionFactory;
import dev.jaxydog.content.power.CustomMetaAction;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.data.SerializableData.Instance;

/** The repeat meta-action */
public class RepeatAction extends CustomMetaAction {

	public RepeatAction(String rawId) {
		super(rawId);
	}

	@Override
	public <T> void execute(Instance data, T t) {
		final ActionFactory<T>.Instance action = data.get("action");
		final int repeat = data.getInt("repeat");

		for (int i = 0; i < repeat; i += 1) {
			action.accept(t);
		}
	}

	@Override
	public <T> CustomActionFactory<T> factory(SerializableDataType<ActionFactory<T>.Instance> type) {
		final SerializableData data = new SerializableData()
			.add("repeat", SerializableDataTypes.INT)
			.add("action", type);

		return new CustomActionFactory<T>(this.getRawId(), data, this::execute);
	}

}
