package dev.jaxydog.astral.utility;

import dev.jaxydog.astral.utility.register.Registerable;
import java.util.HashMap;
import java.util.function.BiFunction;
import net.minecraft.util.DyeColor;

/**
 * Utility class for auto-generating values for each dye color
 * ---
 * This is similar to how dyed amethyst blocks are generated within the old Moonlight mod, only made to be much more generic
 */
public class DyeableSet<T extends Registerable> implements Registerable.Client, Registerable.Main, Registerable.Server {

	/** The set's internal map that contains the created value for each dye color */
	private final HashMap<DyeColor, T> MAP = new HashMap<>(DyeColor.values().length);

	public DyeableSet(String baseRawId, BiFunction<String, DyeColor, T> constructor) {
		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseRawId;
			var value = constructor.apply(id, color);

			this.MAP.put(color, value);
		}
	}

	/** Returns the value associated with the given color */
	public T get(DyeColor color) {
		return this.MAP.get(color);
	}

	/** Always returns null; this should not be used */
	@Override
	public String getRawId() {
		return null;
	}

	@Override
	public void registerClient() {
		for (var value : this.MAP.values()) {
			if (!(value instanceof Registerable.Client)) continue;

			((Registerable.Client) value).registerClient();
		}
	}

	@Override
	public void registerMain() {
		for (var value : this.MAP.values()) {
			if (!(value instanceof Registerable.Main)) continue;

			((Registerable.Main) value).registerMain();
		}
	}

	@Override
	public void registerServer() {
		for (var value : this.MAP.values()) {
			if (!(value instanceof Registerable.Server)) continue;

			((Registerable.Server) value).registerServer();
		}
	}
}
