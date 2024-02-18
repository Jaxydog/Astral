package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.register.Registered;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

/** An extension of a regular action factory that provides additional functionality */
public class CustomActionFactory<T> extends ActionFactory<T> implements Registered {

    /** The custom action factory's inner raw identifier */
    private final String RAW_ID;

    public CustomActionFactory(String rawId, SerializableData data, BiConsumer<SerializableData.Instance, T> effect) {
        super(Astral.getId(rawId), data, effect);

        this.RAW_ID = rawId;
    }

    /** Registers the factory in the given registry */
    public void register(Registry<ActionFactory<T>> registry) {
        Registry.register(registry, this.getRegistryId(), this);
    }

    @Override
    public Identifier getRegistryId() {
        return this.getSerializerId();
    }

    @Override
    public String getRegistryIdPath() {
        return this.RAW_ID;
    }

}
