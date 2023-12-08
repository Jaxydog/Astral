package dev.jaxydog.utility;

import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.minecraft.item.ArmorItem;

public class ArmorMap<T extends Registerable> extends RegisterableMap<ArmorItem.Type, T> {

	public ArmorMap(String rawId, BiFunction<String, ArmorItem.Type, T> constructor) {
		super(rawId, ArmorItem.Type::values, constructor, (type, id) -> id + "_" + type.getName());
	}

}
