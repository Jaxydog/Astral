package dev.jaxydog.utility;

import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.minecraft.util.DyeColor;

public class DyeableMap<T extends Registerable> extends RegisterableMap<DyeColor, T> {

	public DyeableMap(String rawId, BiFunction<String, DyeColor, T> constructor) {
		super(rawId, DyeColor::values, constructor, DyeColor::asString);
	}

}
