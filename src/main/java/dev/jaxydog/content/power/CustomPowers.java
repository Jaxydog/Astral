package dev.jaxydog.content.power;

import dev.jaxydog.content.data.CustomData;
import dev.jaxydog.content.data.ScaleOperation;
import dev.jaxydog.content.power.custom.ScalePower;
import dev.jaxydog.utility.register.ContentContainer;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;

/** Contains definitions for all custom powers */
public final class CustomPowers extends ContentContainer {

	public static final CustomPowerFactory<ScalePower> SCALE = new CustomPowerFactory<ScalePower>("scale",
		new SerializableData().add("width", SerializableDataTypes.FLOAT, 1F)
			.add("height", SerializableDataTypes.FLOAT, 1F)
			.add("reach", SerializableDataTypes.FLOAT, 1F)
			.add("motion", SerializableDataTypes.FLOAT, 1F)
			.add("jump", SerializableDataTypes.FLOAT, 1F)
			.add("reset_on_lost", SerializableDataTypes.BOOLEAN, true)
			.add("operation", CustomData.SCALE_OPERATION, ScaleOperation.MULTIPLICATIVE),
		data -> (type, entity) -> new ScalePower(type,
			entity,
			data.getFloat("width"),
			data.getFloat("height"),
			data.getFloat("reach"),
			data.getFloat("motion"),
			data.getFloat("jump"),
			data.getBoolean("reset_on_lost"),
			data.get("operation")
		)
	).allowCondition();

}
