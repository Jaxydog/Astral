package dev.jaxydog.utility.register;

import dev.jaxydog.register.Registered;
import net.minecraft.util.DyeColor;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class DyeableMap<T extends Registered> extends RegisteredMap<DyeColor, T> {

    private static final List<DyeColor> ORDER = List.of(
        DyeColor.WHITE,
        DyeColor.LIGHT_GRAY,
        DyeColor.GRAY,
        DyeColor.BLACK,
        DyeColor.BROWN,
        DyeColor.RED,
        DyeColor.ORANGE,
        DyeColor.YELLOW,
        DyeColor.LIME,
        DyeColor.GREEN,
        DyeColor.CYAN,
        DyeColor.LIGHT_BLUE,
        DyeColor.BLUE,
        DyeColor.PURPLE,
        DyeColor.MAGENTA,
        DyeColor.PINK
    );

    public DyeableMap(String rawId, BiFunction<String, DyeColor, T> constructor) {
        super(rawId, constructor);
    }

    @Override
    public final Set<DyeColor> keys() {
        return Set.of(DyeColor.values());
    }

    @Override
    public final String getIdPath(DyeColor key) {
        return String.format("%s_%s", key.asString(), this.getRegistryIdPath());
    }

    @Override
    protected final int compareKeys(DyeColor a, DyeColor b) {
        return Integer.compare(ORDER.indexOf(a), ORDER.indexOf(b));
    }

}
