package dev.jaxydog.utility;

import dev.jaxydog.register.Registered;
import net.minecraft.util.DyeColor;

import java.util.Set;
import java.util.function.BiFunction;

public class DyeableMap<T extends Registered> extends RegisteredMap<DyeColor, T> {

	public DyeableMap(String rawId, BiFunction<String, DyeColor, T> constructor) {
		super(rawId, constructor);
	}

	@Override
	public final Set<DyeColor> keys() {
		return Set.of(DyeColor.values());
	}

	@Override
	public final String getIdPath(DyeColor key) {
		return String.format("%s_%s", key.asString(), this.getIdPath());
	}

	@Override
	protected final int compareKeys(DyeColor a, DyeColor b) {
		return a.compareTo(b);
	}

}
