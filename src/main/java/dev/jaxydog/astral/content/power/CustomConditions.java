package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.content.data.CustomData;
import dev.jaxydog.astral.content.data.custom.MoonPhase;
import dev.jaxydog.astral.utility.register.AutoRegister;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import java.util.List;
import net.minecraft.entity.Entity;

/** Contains definitions for all custom conditions */
@AutoRegister
public final class CustomConditions {

	public static final CustomConditionFactory<Entity> MOON_PHASE = new CustomConditionFactory<>(
		"moon_phase",
		new SerializableData()
			.add("phase", CustomData.MOON_PHASE, MoonPhase.NONE)
			.add("phases", CustomData.MOON_PHASES, List.of()),
		(data, entity) -> {
			var phase = data.<MoonPhase>get("phase");
			var phases = data.<List<MoonPhase>>get("phases");

			if (phase != MoonPhase.NONE) return phase.isCurrent(entity.getWorld());

			for (var possible : phases) {
				if (possible.isCurrent(entity.getWorld())) return true;
			}

			return false;
		},
		() -> ApoliRegistries.ENTITY_CONDITION
	);

	private CustomConditions() {}
}
