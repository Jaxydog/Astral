package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.content.power.condition.DistanceCondition;
import dev.jaxydog.astral.content.power.condition.MoonPhaseCondition;
import dev.jaxydog.astral.content.power.condition.UnobstructedBlockInRadiusCondition;
import dev.jaxydog.astral.register.ContentRegistrar;

/** Contains definitions for all custom conditions */
@SuppressWarnings("unused")
public final class CustomConditions extends ContentRegistrar {

    public static final DistanceCondition DISTANCE = new DistanceCondition("distance");

    public static final MoonPhaseCondition MOON_PHASE = new MoonPhaseCondition("moon_phase");

    public static final UnobstructedBlockInRadiusCondition UNOBSTRUCTED_BLOCK_IN_RADIUS = new UnobstructedBlockInRadiusCondition(
        "unobstructed_block_in_radius");

}
