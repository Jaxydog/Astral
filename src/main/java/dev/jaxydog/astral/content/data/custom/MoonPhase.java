package dev.jaxydog.astral.content.data.custom;

import net.minecraft.world.WorldAccess;

/** Represents a possible in-game moon phase */
public enum MoonPhase {
	NONE(-1),
	FULL_MOON(0),
	WANING_GIBBUS(1),
	THIRD_QUARTER(2),
	WANING_CRESCENT(3),
	NEW_MOON(4),
	WAXING_CRESCENT(5),
	FIRST_QUARTER(6),
	WAXING_GIBBUS(7);

	/** The moon phase's internal identifier */
	private final int ID;

	private MoonPhase(int id) {
		this.ID = id;
	}

	/** Returns a moon phase from the given phase identifier */
	public static MoonPhase from(String id) {
		for (var phase : MoonPhase.values()) {
			if (phase.getName() == id) return phase;
		}

		return MoonPhase.NONE;
	}

	/** Returns the moon phase's internal identifier */
	public int getId() {
		return this.ID;
	}

	/** Returns the name of the moon phase */
	public String getName() {
		return this.toString().toLowerCase();
	}

	/** Returns whether the value is the current moon phase */
	public boolean isCurrent(WorldAccess world) {
		return world.getMoonPhase() == this.getId();
	}
}
