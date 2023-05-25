package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.content.power.custom.ScalePower;
import dev.jaxydog.astral.utility.register.AutoRegister;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;

/** Contains definitions for all custom powers */
@AutoRegister
public final class CustomPowers {

	public static final CustomPowerFactory<Power> SCALE = new CustomPowerFactory<>(
		"scale",
		new SerializableData()
			.add("width", SerializableDataTypes.FLOAT, 1.0F)
			.add("height", SerializableDataTypes.FLOAT, 1.0F)
			.add("reach", SerializableDataTypes.FLOAT, 1.0F)
			.add("motion", SerializableDataTypes.FLOAT, 1.0F)
			.add("jump", SerializableDataTypes.FLOAT, 1.0F)
			.add("reset_on_lost", SerializableDataTypes.BOOLEAN, true),
		data ->
			(type, entity) -> {
				var width = data.getFloat("width");
				var height = data.getFloat("height");
				var reach = data.getFloat("reach");
				var motion = data.getFloat("motion");
				var jump = data.getFloat("jump");
				var reset = data.getBoolean("reset_on_lost");

				return new ScalePower(type, entity, width, height, reach, motion, jump, reset);
			}
	)
		.allowCondition();

	private CustomPowers() {}
}
