package dev.jaxydog.content.power.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.content.power.CustomCondition;
import dev.jaxydog.content.power.CustomConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableData.Instance;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class DistanceCondition extends CustomCondition<Entity> {

	public DistanceCondition(String rawId) {
		super(rawId);
	}

	@Override
	public boolean check(Instance data, Entity entity) {
		final List<Double> targetList = data.get("position");

		if (targetList.size() < 3) {
			Astral.LOGGER.warn("Expected three position coordinates!");

			return false;
		}

		final Vec3d targetPos = new Vec3d(targetList.get(0), targetList.get(1), targetList.get(2));
		final double distance = Math.sqrt(entity.getPos().squaredDistanceTo(targetPos));

		return data.<Comparison>get("comparison").compare(distance, data.getDouble("compare_to"));
	}

	@Override
	public CustomConditionFactory<Entity> factory() {
		final SerializableData data = new SerializableData().add("position", SerializableDataTypes.DOUBLES)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.DOUBLE);

		return new CustomConditionFactory<>(this.getRawId(), data, this::check);
	}

	@Override
	public Registry<ConditionFactory<Entity>> registry() {
		return ApoliRegistries.ENTITY_CONDITION;
	}

}
