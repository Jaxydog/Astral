package dev.jaxydog.utility;

import java.util.Set;
import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.minecraft.util.DyeColor;

public class DyeableMap<T extends Registerable> extends RegisterableMap<DyeColor, T> {

	public DyeableMap(String rawId, BiFunction<String, DyeColor, T> constructor) {
		super(rawId, constructor);
	}

	@Override
	public final Set<DyeColor> keys() {
		return Set.of(DyeColor.values());
	}

	@Override
	public final String getRawId(DyeColor key) {
		return String.format("%s_%s", key.asString(), this.getRawId());
	}

	@Override
	protected final int compareKeys(DyeColor a, DyeColor b) {
		return a.compareTo(b);
	}

}
