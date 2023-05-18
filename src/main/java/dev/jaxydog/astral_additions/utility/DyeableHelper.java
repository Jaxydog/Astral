package dev.jaxydog.astral_additions.utility;

import dev.jaxydog.astral_additions.utility.Functions.QuadFunction;
import dev.jaxydog.astral_additions.utility.Functions.QuintFunction;
import dev.jaxydog.astral_additions.utility.Functions.TriFunction;
import java.util.HashMap;
import java.util.function.BiFunction;
import net.minecraft.util.DyeColor;

/**
 * Utility class for auto-generating values for each dye color
 * ---
 * This is similar to how dyeable amethyst clusters are generated in the Astral mod, but made to be much more generic
 */
public class DyeableHelper<T extends Registerable>
	implements Registerable.Client, Registerable.Main, Registerable.Server {

	/** The helper's inner map containing the value for each dye color */
	private final HashMap<DyeColor, T> MAP = new HashMap<>(DyeColor.values().length);

	// let's hope that we don't need more than 4 generic arguments lol
	public <A> DyeableHelper(String baseId, A arg1, BiFunction<String, A, T> constructor) {
		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, arg1);

			this.MAP.put(color, value);
		}
	}

	public <A, B> DyeableHelper(String baseId, A arg1, B arg2, TriFunction<String, A, B, T> constructor) {
		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, arg1, arg2);

			this.MAP.put(color, value);
		}
	}

	public <A, B, C> DyeableHelper(
		String baseId,
		A arg1,
		B arg2,
		C arg3,
		QuadFunction<String, A, B, C, T> constructor
	) {
		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, arg1, arg2, arg3);

			this.MAP.put(color, value);
		}
	}

	public <A, B, C, D> DyeableHelper(
		String baseId,
		A arg1,
		B arg2,
		C arg3,
		D arg4,
		QuintFunction<String, A, B, C, D, T> constructor
	) {
		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, arg1, arg2, arg3, arg4);

			this.MAP.put(color, value);
		}
	}

	/** This always will return `null` and should not be used */
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
