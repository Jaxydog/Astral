package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.register.Registerable;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** An extension of a regular action factory that provides additional functionality */
public class CustomActionFactory<T> extends ActionFactory<T> implements Registerable.Main {

	/** The custom action factory's inner raw identifier */
	private final String RAW_ID;
	/** Function that supplies the action factory's registry instance */
	private final Supplier<Registry<ActionFactory<T>>> REGISTRY_SUPPLIER;

	public CustomActionFactory(
		String rawId,
		SerializableData data,
		BiConsumer<SerializableData.Instance, T> effect,
		Supplier<Registry<ActionFactory<T>>> registry
	) {
		super(Astral.getId(rawId), data, effect);
		this.RAW_ID = rawId;
		this.REGISTRY_SUPPLIER = registry;
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public Identifier getId() {
		return this.getSerializerId();
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();
		Registry.register(this.REGISTRY_SUPPLIER.get(), this.getId(), this);
	}
}
