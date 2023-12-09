package dev.jaxydog.utility;

import java.util.Set;
import java.util.function.BiFunction;
import dev.jaxydog.utility.register.Registerable;
import net.minecraft.item.ArmorItem;

public class ArmorMap<T extends Registerable> extends RegisterableMap<ArmorItem.Type, T> {

	public ArmorMap(String rawId, BiFunction<String, ArmorItem.Type, T> constructor) {
		super(rawId, constructor);
	}

	@Override
	public final Set<ArmorItem.Type> keys() {
		return Set.of(ArmorItem.Type.values());
	}

	@Override
	public final String getRawId(ArmorItem.Type key) {
		return String.format("%s_%s", this.getRawId(), key.getName());
	}

	@Override
	protected final int compareKeys(ArmorItem.Type a, ArmorItem.Type b) {
		return a.compareTo(b);
	}

}
