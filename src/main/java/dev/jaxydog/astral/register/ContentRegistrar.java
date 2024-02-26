package dev.jaxydog.astral.register;

import dev.jaxydog.astral.Astral;

import java.io.InvalidClassException;
import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.*;

/** Provides field auto-registration for the implementing class. */
public abstract class ContentRegistrar implements Registered.All, Generated {

    private void registerFields(RegistrationEnvironment environment) {
        // This iterates over all publicly defined fields within the implementing class.
        for (final Field field : this.getClass().getFields()) {
            final int modifiers = field.getModifiers();

            // Asserts that the checked field is `public static final`.
            if (!(isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers))) continue;
            // Asserts, for a field of type `T`, that `T instanceof Registered.Environment`.
            if (!environment.getInterface().isAssignableFrom(field.getDeclaringClass())) continue;
            // Asserts that the field should not be ignored in the current environment.
            if (field.isAnnotationPresent(IgnoreRegistration.class)) {
                if (environment.shouldIgnore(field.getAnnotation(IgnoreRegistration.class))) continue;
            }

            try {
                final Object registerable = field.get(null);

                // Re-asserts that the returned value is an instance of `Registered`.
                if (environment.getInterface().isInstance(registerable)) {
                    // Which means that this cast is safe and should never throw.
                    environment.register((Registered) registerable);
                }
            } catch (final IllegalAccessException | InvalidClassException exception) {
                Astral.LOGGER.error(exception.toString());
            }
        }
    }

    @Override
    public final String getRegistryIdPath() {
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
