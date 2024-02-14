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

package dev.jaxydog.astral.registry;

import dev.jaxydog.astral.Astral;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Implements the automatic registration system for extending classes.
 * <p>
 * Extending classes should typically be {@code final} and only contain subclasses or {@code public static final}
 * fields. They should be singletons with their only instance being placed within the
 * {@link dev.jaxydog.astral.content.AstralContent} class.
 *
 * @author Jaxydog
 * @see Registered
 * @see IgnoreRegistration
 */
public abstract class ContentRegistrar implements Registered.Mod, Registered.Generated {

    /**
     * Registers all publicly-defined static constants within the extending class within the specified environment.
     * <p>
     * Attempting to register values within a mis-matched environment will result in a run-time crash.
     *
     * @param environment The target environment.
     */
    private void automaticallyRegister(RegistryEnvironment environment) {
        // This iterates over all publicly-defined fields within the extending class.
        for (final Field field : this.getClass().getFields()) {
            final int modifiers = field.getModifiers();

            // Asserts that the current field is both static and final.
            if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) continue;
            // Asserts that the current field is of or extends the environment's associated interface.
            if (!environment.getInterface().isAssignableFrom(field.getDeclaringClass())) continue;
            // Asserts that the current field is not ignored within the current environment.
            if (field.isAnnotationPresent(IgnoreRegistration.class)) {
                final IgnoreRegistration annotation = field.getAnnotation(IgnoreRegistration.class);

                if (environment.isIgnored(annotation)) continue;
            }

            try {
                final Object value = field.get(null);

                // Re-assert that the field is of or extends the environment's associated interface.
                if (environment.getInterface().isInstance(value)) {
                    environment.registerValue((Registered) value);
                } else {
                    Astral.getLogger().warn("Expected a value of type {}", environment.getInterface().getSimpleName());
                }
            } catch (IllegalAccessException | IllegalArgumentException exception) {
                Astral.getLogger().error(exception.getLocalizedMessage());
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
    public void register() {
        this.automaticallyRegister(RegistryEnvironment.COMMON);
    }

    @Override
    public void registerClient() {
        this.automaticallyRegister(RegistryEnvironment.CLIENT);
    }

    @Override
    public void registerServer() {
        this.automaticallyRegister(RegistryEnvironment.SERVER);
    }

    @Override
    public void generate() {
        this.automaticallyRegister(RegistryEnvironment.GENERATOR);
    }

}
