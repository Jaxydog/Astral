package dev.jaxydog.utility;

import dev.jaxydog.utility.register.Registerable;
import net.minecraft.item.ArmorItem.Type;

import java.util.Set;
import java.util.function.BiFunction;

public class ArmorMap<T extends Registerable> extends RegisterableMap<Type, T> {

	public ArmorMap(String rawId, BiFunction<String, Type, T> constructor) {
		super(rawId, constructor);
	}

	@Override
	protected final int compareKeys(Type a, Type b) {
		return a.compareTo(b);
	}

	@Override
	public final Set<Type> keys() {
		return Set.of(Type.values());
	}

	@Override
	public final String getRawId(Type key) {
		return String.format("%s_%s", this.getRawId(), key.getName());
	}

}
