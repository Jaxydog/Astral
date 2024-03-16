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
import dev.jaxydog.astral.content.CustomContent;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;

/**
 * Implements the automatic registration system for extending classes.
 * <p>
 * Extending classes should typically be {@code final} and only contain subclasses or {@code public static final}
 * fields. They should be singletons with their only instance being placed within the {@link CustomContent} class.
 *
 * @author Jaxydog
 * @see Registered
 * @see IgnoreRegistration
 * @since 1.5.0
 */
public abstract class ContentRegistrar implements Registered.All, Registered.Generated {

    /**
     * Used to sort registered fields by their assigned priorities.
     *
     * @since 2.0.0
     */
    private static final Comparator<Field> PRIORITY = Comparator.comparingInt((Field field) -> {
        if (field.isAnnotationPresent(RegistrationPriority.class)) {
            return field.getAnnotation(RegistrationPriority.class).value();
        } else {
            return 0; // Assume a value of zero for any unset field.
        }
    }).reversed();

    /**
     * Registers all publicly-defined static constants within the extending class within the specified environment.
     * <p>
     * Attempting to register values within a mis-matched environment will result in a run-time crash.
     *
     * @param environment The target environment.
     *
     * @since 2.0.0
     */
    private void registerFields(Environment environment) {
        // Streams the fields and sorts them by their configured priority.
        final List<Field> fields = Arrays.stream(this.getClass().getFields()).sorted(PRIORITY).toList();

        // This iterates over all publicly defined fields within the implementing class.
        for (final Field field : fields) {
            final int modifiers = field.getModifiers();

            // Asserts that the checked field is both static and final.
            // We can assume that it is public, since `getFields` only returns publicly available fields.
            if (!isStatic(modifiers) || !isFinal(modifiers)) continue;
            // Asserts, for a field of type `T`, that `T instanceof Registered.<Environment>`.
            if (!environment.getInterface().isAssignableFrom(field.getType())) continue;
            // Asserts that the field should not be ignored in the current environment.
            if (field.isAnnotationPresent(IgnoreRegistration.class)) {
                final IgnoreRegistration annotation = field.getAnnotation(IgnoreRegistration.class);

                if (environment.isIgnored(annotation)) continue;
            }

            try {
                final Object registerable = field.get(null);

                // We already asserted that the returned value is an instance of `Registered`.
                environment.registerValue((Registered) registerable);
            } catch (final IllegalAccessException | IllegalArgumentException exception) {
                // If something fails, output the exception message and intentionally ignore the field.
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

    // The methods below should be called within their respective mod entry-points, otherwise an entire environment will
    // be cut off from initialization.

    @Override
    public final void registerCommon() {
        this.registerFields(Environment.COMMON);
    }

    @Override
    public final void registerClient() {
        this.registerFields(Environment.CLIENT);
    }

    @Override
    public final void registerServer() {
        this.registerFields(Environment.SERVER);
    }

    @Override
    public void generate() {
        this.registerFields(Environment.GENERATOR);
    }

}
