package dev.jaxydog.astral.utility.injected;

public interface AstralLivingEntity {

    default boolean astral$ignoresChallengeScaling() {
        return false;
    }

    default boolean astral$forcesChallengeScaling() {
        return false;
    }

}
