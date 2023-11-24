package dev.jaxydog.content.power.custom;

import java.util.List;
import dev.jaxydog.Astral;
import dev.jaxydog.content.power.CustomCondition;
import dev.jaxydog.content.power.CustomConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Vec3d;

public class DistanceCondition extends CustomCondition<Entity> {

	public DistanceCondition(String rawId) {
		super(rawId);
	}

	private Vec3d parsePosition(Vec3d pos, String string) {
		final List<String> parts = List.of(string.trim().split(" ")).subList(0, 3);

		if (parts.size() != 3) {
			Astral.LOGGER.warn("Invalid position provided: '" + string + "'");

			return pos;
		}

		final double x = this.parsePosition(pos.x, parts.get(0));
		final double y = this.parsePosition(pos.y, parts.get(1));
		final double z = this.parsePosition(pos.z, parts.get(2));

		return new Vec3d(x, y, z);
	}

	private double parsePosition(double pos, String string) {
		if (string.equals("~") || string.equals("^")) {
			return pos;
		}

		try {
			return Double.parseDouble(string);
		} catch (Exception exception) {
			Astral.LOGGER.warn(exception.getLocalizedMessage());
			return pos;
		}
	}

	@Override
	public boolean check(Instance data, Entity entity) {
		final Vec3d entityPos = entity.getPos();
		final Vec3d targetPos = this.parsePosition(entityPos, data.getString("position"));
		final double compareTo = data.getDouble("compare_to");
		final double distance = Math.sqrt(entityPos.squaredDistanceTo(targetPos));

		return data.<Comparison>get("comparison").compare(distance, compareTo);
	}

	@Override
	public CustomConditionFactory<Entity> factory() {
		final SerializableData data =
				new SerializableData().add("position", SerializableDataTypes.STRING)
						.add("comparison", ApoliDataTypes.COMPARISON)
						.add("compare_to", SerializableDataTypes.DOUBLE);

		return new CustomConditionFactory<>(this.getRawId(), data, this::check);
	}

	@Override
	public Registry<ConditionFactory<Entity>> registry() {
		return ApoliRegistries.ENTITY_CONDITION;
	}

}
