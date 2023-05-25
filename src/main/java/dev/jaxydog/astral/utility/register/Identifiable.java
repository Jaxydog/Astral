package dev.jaxydog.astral.utility.register;

import dev.jaxydog.astral.Astral;
import net.minecraft.util.Identifier;

/** A type that has an asoociated `Identifier` value */
public interface Identifiable {
	/** Returns the value's raw identifier; used as the identifier's path by default */
	public String getRawId();

	/** Returns the value's associated identifier */
	public default Identifier getId() {
		return Astral.getId(this.getRawId());
	}
}
