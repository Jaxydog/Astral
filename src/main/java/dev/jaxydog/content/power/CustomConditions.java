package dev.jaxydog.content.power;

import dev.jaxydog.content.power.custom.DistanceCondition;
import dev.jaxydog.content.power.custom.MoonPhaseCondition;
import dev.jaxydog.register.ContentRegistrar;

/** Contains definitions for all custom conditions */
public final class CustomConditions extends ContentRegistrar {

	public static final DistanceCondition DISTANCE = new DistanceCondition("distance");

	public static final MoonPhaseCondition MOON_PHASE = new MoonPhaseCondition("moon_phase");

}
