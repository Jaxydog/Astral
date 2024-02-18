/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023–2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.register;

import dev.jaxydog.astral.Astral;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;

/**
 * Implements the automatic registration system for extending classes.
 * <p>
 * Extending classes should typically be {@code final} and only contain subclasses or {@code public static final}
 * fields. They should be singletons with their only instance being placed within the
 * {@link dev.jaxydog.astral.content.CustomContent} class.
 *
 * @author Jaxydog
 * @see Registered
 * @see IgnoreRegistration
 */
public abstract class ContentRegistrar implements Registered.All, Registered.Generated {

    /**
     * Registers all publicly-defined static constants within the extending class within the specified environment.
     * <p>
     * Attempting to register values within a mis-matched environment will result in a run-time crash.
     *
     * @param environment The target environment.
     */
    private void registerFields(RegistrationEnvironment environment) {
        // This iterates over all publicly defined fields within the implementing class.
        for (final Field field : this.getClass().getFields()) {
            final int modifiers = field.getModifiers();

            // Asserts that the checked field is both static and final.
            // We can assume that it is public, since `getFields` only returns publicly available fields.
            if (!isStatic(modifiers) || !isFinal(modifiers)) continue;
            // Asserts, for a field of type `T`, that `T instanceof Registered.Environment`.
            if (!environment.getInterface().isAssignableFrom(field.getDeclaringClass())) continue;
            // Asserts that the field should not be ignored in the current environment.
            if (field.isAnnotationPresent(IgnoreRegistration.class)) {
                final IgnoreRegistration annotation = field.getAnnotation(IgnoreRegistration.class);

                if (environment.isIgnored(annotation)) continue;
            }

            try {
                final Object registerable = field.get(null);

                // Re-asserts that the returned value is an instance of `Registered`.
                if (environment.getInterface().isInstance(registerable)) {
                    // Which means that this cast is safe and should never throw.
                    environment.registerValue((Registered) registerable);
                } else {
                    Astral.LOGGER.warn("Expected a value of type {}", environment.getInterface().getSimpleName());
                }
            } catch (final IllegalAccessException | IllegalArgumentException exception) {
                Astral.LOGGER.error(exception.getLocalizedMessage());
            }
        }
    }

    @Override
    public String getRegistryPath() {
        throw new UnsupportedOperationException("This cannot be called on instances of " + this.getClass().getName());
    }

    @Override
    public Identifier getRegistryId() {
        throw new UnsupportedOperationException("This cannot be called on instances of " + this.getClass().getName());
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
        this.registerFields(RegistrationEnvironment.GENERATOR);
    }

}
