package dev.jaxydog.astral_additions.utility;

import dev.jaxydog.astral_additions.AstralAdditions;
import net.minecraft.util.Identifier;

/** A value that has an associated identifier */
public interface Identifiable {
	/** Returns the value's raw identifier; this is used, by default, as the path in the value's actual identifier */
	public String getRawId();

	/** Returns the value's identifier */
	public default Identifier getId() {
		return AstralAdditions.getId(this.getRawId());
	}
}
