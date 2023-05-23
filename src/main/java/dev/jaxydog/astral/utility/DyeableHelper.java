package dev.jaxydog.astral.utility;

import dev.jaxydog.astral.utility.Functions.QuadFunction;
import dev.jaxydog.astral.utility.Functions.QuintFunction;
import dev.jaxydog.astral.utility.Functions.TriFunction;
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

	private DyeableHelper(HashMap<DyeColor, T> map) {
		this.MAP.putAll(map);
	}

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

	public static <A extends Registerable, T extends Registerable> DyeableHelper<T> fromDyeable(
		String baseId,
		DyeableHelper<A> dyeable,
		TriFunction<String, DyeColor, DyeableHelper<A>, T> constructor
	) {
		var map = new HashMap<DyeColor, T>(DyeColor.values().length);

		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, color, dyeable);

			map.put(color, value);
		}

		return new DyeableHelper<>(map);
	}

	public static <A extends Registerable, B, T extends Registerable> DyeableHelper<T> fromDyeable(
		String baseId,
		DyeableHelper<A> dyeable,
		B arg1,
		QuadFunction<String, DyeColor, DyeableHelper<A>, B, T> constructor
	) {
		var map = new HashMap<DyeColor, T>(DyeColor.values().length);

		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, color, dyeable, arg1);

			map.put(color, value);
		}

		return new DyeableHelper<>(map);
	}

	public static <A extends Registerable, B, C, T extends Registerable> DyeableHelper<T> fromDyeable(
		String baseId,
		DyeableHelper<A> dyeable,
		B arg1,
		C arg2,
		QuintFunction<String, DyeColor, DyeableHelper<A>, B, C, T> constructor
	) {
		var map = new HashMap<DyeColor, T>(DyeColor.values().length);

		for (var color : DyeColor.values()) {
			var id = color.asString() + "_" + baseId;
			var value = constructor.apply(id, color, dyeable, arg1, arg2);

			map.put(color, value);
		}

		return new DyeableHelper<>(map);
	}

	/** Returns a value from the helper */
	public T get(DyeColor color) {
		return this.MAP.get(color);
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
