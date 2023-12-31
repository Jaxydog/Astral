package dev.jaxydog.register;

import dev.jaxydog.Astral;

import java.io.InvalidClassException;
import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.*;

/** Provides field auto-registration for the implementing class. */
public abstract class ContentRegistrar implements Registered.All, Generated {

	private void registerFields(RegistrationEnvironment environment) {
		for (final Field field : this.getClass().getFields()) {
			final int modifiers = field.getModifiers();

			if (!environment.getInterface().isAssignableFrom(field.getDeclaringClass())) continue;
			if (!(isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers))) continue;
			if (field.isAnnotationPresent(IgnoreRegistration.class)) {
				if (environment.shouldIgnore(field.getAnnotation(IgnoreRegistration.class))) continue;
			}

			try {
				final Object registerable = field.get(null);

				if (!environment.getInterface().isInstance(registerable)) {
					environment.register((Registered) registerable);
				}
			} catch (final IllegalAccessException | InvalidClassException exception) {
				Astral.LOGGER.error(exception.toString());
			}
		}
	}

	@Override
	public final String getIdPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void register() {
		this.registerFields(RegistrationEnvironment.COMMON);
	}

	@Override
	public final void registerClient() {
		this.registerFields(RegistrationEnvironment.CLIENT);
	}

	@Override
	public final void registerServer() {
		this.registerFields(RegistrationEnvironment.SERVER);
	}

	@Override
	public void generate() {
		this.registerFields(RegistrationEnvironment.DATA_GEN);
	}

}
