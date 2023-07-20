package dev.jaxydog.content.power;

import dev.jaxydog.content.power.custom.MoonPhaseCondition;
import dev.jaxydog.utility.register.ContentContainer;

/** Contains definitions for all custom conditions */
public final class CustomConditions extends ContentContainer {

	public static final MoonPhaseCondition MOON_PHASE = new MoonPhaseCondition("moon_phase");

}
