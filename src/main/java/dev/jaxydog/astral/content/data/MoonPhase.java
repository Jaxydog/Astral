package dev.jaxydog.astral.content.data;

import net.minecraft.world.LunarWorldView;

/** Represents the phases of the moon */
public enum MoonPhase {

	/** An invalid phase */
	NONE(-1),
	/** A full moon */
	FULL_MOON(0),
	/** A waning gibbous */
	WANING_GIBBOUS(1),
	/** A third quarter */
	THIRD_QUARTER(2),
	/** A waning crescent */
	WANING_CRESCENT(3),
	/** A new moon */
	NEW_MOON(4),
	/** A waxing crescent */
	WAXING_CRESCENT(5),
	/** A first quarter */
	FIRST_QUARTER(6),
	/** A waxing gibbous */
	WAXING_GIBBOUS(7);

	/** The inner phase identifier */
	private final int ID;

	MoonPhase(int id) {
		this.ID = id;
	}

	/** Returns the moon phase with the given name */
	public static MoonPhase from(String name) {
		final String lower = name.toLowerCase();

		for (final MoonPhase phase : values()) {
			if (phase.getName().equals(lower)) return phase;
		}

		return NONE;
	}

	/** Returns the phase's name */
	public String getName() {
		return this.toString().toLowerCase();
	}

	/** Returns the value's inner phase identifier */
	public int getId() {
		return this.ID;
	}

	/** Returns whether the phase is the current world's moon phase */
	public boolean isCurrent(LunarWorldView world) {
		return world.getMoonPhase() == this.ID;
	}

}
