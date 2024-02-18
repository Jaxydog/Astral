package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.register.Registered;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.registry.Registry;

/** Abstract class for implementing actions */
@SuppressWarnings("unused")
public abstract class CustomAction<T> implements Registered.Common {

    /** The custom action's inner raw identifier */
    private final String RAW_ID;

    public CustomAction(String rawId) {
        this.RAW_ID = rawId;
    }

    /** Executes the action */
    public abstract void execute(Instance data, T value);

    @Override
    public String getRegistryPath() {
        return this.RAW_ID;
    }

    @Override
    public void register() {
        this.factory().register(this.registry());
    }

    /** Returns the action's factory */
    public abstract CustomActionFactory<T> factory();

    /** Returns the action's registry */
    public abstract Registry<ActionFactory<T>> registry();

}
