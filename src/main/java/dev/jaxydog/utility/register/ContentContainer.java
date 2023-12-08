package dev.jaxydog.utility.register;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isFinal;
import java.lang.reflect.Field;
import org.jetbrains.annotations.Nullable;
import dev.jaxydog.Astral;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

/** Represents a class that exists solely to define content */
public abstract class ContentContainer implements Registerable.All {

	/** Automatically registers all possible fields within the class */
	private final void register(Env env, @Nullable Pack pack) {
		final Class<? extends ContentContainer> source = this.getClass();

		for (final Field field : source.getFields()) {
			this.register(env, pack, field);
		}
	}

	/** Automatically registers a field if possible */
	private final void register(Env env, @Nullable Pack pack, Field field) {
		final int modifiers = field.getModifiers();

		if (!isPublic(modifiers) || !isStatic(modifiers) || !isFinal(modifiers)) {
			return;
		}

		if (field.isAnnotationPresent(Skip.class)) {
			final Skip annotation = field.getAnnotation(Skip.class);

			if (env.isSkipped(annotation)) {
				return;
			}
		}

		try {
			final Object value = field.get(null);

			if (env.getInterface().isInstance(value)) {
				if (env == Env.DATAGEN) {
					env.getMethod().invoke(value, pack);
				} else {
					env.getMethod().invoke(value);
				}
			}
		} catch (final Exception e) {
			Astral.LOGGER.error(e.toString());
		}
	}

	@Override
	public final String getRawId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void registerMain() {
		this.register(Env.MAIN, null);
	}

	@Override
	public final void registerClient() {
		this.register(Env.CLIENT, null);
	}

	@Override
	public final void registerServer() {
		this.register(Env.SERVER, null);
	}

	@Override
	public final void registerDatagen(Pack pack) {
		this.register(Env.DATAGEN, pack);
	}

}
