package dev.jaxydog.content.power;

import dev.jaxydog.content.power.condition.DistanceCondition;
import dev.jaxydog.content.power.condition.MoonPhaseCondition;
import dev.jaxydog.content.power.condition.UnobstructedBlockInRadiusCondition;
import dev.jaxydog.register.ContentRegistrar;

/** Contains definitions for all custom conditions */
public final class CustomConditions extends ContentRegistrar {

    public static final DistanceCondition DISTANCE = new DistanceCondition("distance");

    public static final MoonPhaseCondition MOON_PHASE = new MoonPhaseCondition("moon_phase");

    public static final UnobstructedBlockInRadiusCondition UNOBSTRUCTED_BLOCK_IN_RADIUS = new UnobstructedBlockInRadiusCondition(
        "unobstructed_block_in_radius");

}
