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

import dev.jaxydog.astral.register.Registered.Client;
import dev.jaxydog.astral.register.Registered.Common;
import dev.jaxydog.astral.register.Registered.Generated;
import dev.jaxydog.astral.register.Registered.Server;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.Consumer;

/**
 * Represents a possible mod registration environment and its associated interface.
 * <p>
 * This is used to share common functionality during the automatic registration process.
 * <p>
 * You almost certainly do not need to use this directly.
 *
 * @author Jaxydog
 * @see Registered
 * @since 2.0.0
 */
@Internal
public enum Environment {

    /**
     * Represents both the client <i>and</i> the server environments.
     * <p>
     * Note that this is distinctly different from {@link #CLIENT} and {@link #SERVER}.
     * <p>
     * The associated interface of this environment is {@link Common}.
     *
     * @since 2.0.0
     */
    COMMON(Common.class, registered -> ((Common) registered).registerCommon()),
    /**
     * Represents the game client environment.
     * <p>
     * The associated interface of this environment is {@link Client}.
     *
     * @since 2.0.0
     */
    CLIENT(Client.class, registered -> ((Client) registered).registerClient()),
    /**
     * Represents the game server environment.
     * <p>
     * The associated interface of this environment is {@link Server}.
     *
     * @since 2.0.0
     */
    SERVER(Server.class, registered -> ((Server) registered).registerServer()),
    /**
     * Represents the data generation environment.
     * <p>
     * The associated interface of this environment is {@link Generated}.
     *
     * @since 2.0.0
     */
    GENERATOR(Generated.class, registered -> ((Generated) registered).generate());

    /**
     * This environment's associated {@link Registered} interface.
     * <p>
     * This is used for runtime type checking and coercion.
     *
     * @since 2.0.0
     */
    private final Class<? extends Registered> registeredInterface;
    /**
     * A callback function that takes an instance of {@link Registered} and calls its registration method.
     * <p>
     * It's assumed that the value provided will always match the expected type, and as such this will preform an
     * unchecked cast into the associated interface.
     *
     * @since 2.0.0
     */
    private final Consumer<? super Registered> consumeAndRegister;

    /**
     * Creates a new registry environment.
     *
     * @param registeredInterface The interface associated with this environment.
     * @param consumeAndRegister A callback that takes a value and registers it.
     *
     * @since 2.0.0
     */
    Environment(
        Class<? extends Registered> registeredInterface, Consumer<? super Registered> consumeAndRegister
    ) {
        this.registeredInterface = registeredInterface;
        this.consumeAndRegister = consumeAndRegister;
    }

    /**
     * Returns this environment's associated {@link Registered} interface.
     *
     * @return An extension of {@link Registered}.
     *
     * @since 2.0.0
     */
    public final Class<? extends Registered> getInterface() {
        return this.registeredInterface;
    }

    /**
     * Registers the provided value within this environment.
     * <p>
     * This method blindly takes any instance of {@link Registered} and attempts to cast it into its associated type.
     * Extra care should be taken to ensure that the value is of the proper interface.
     *
     * @param value The value to be registered.
     *
     * @throws IllegalArgumentException If the provided value is not a valid instance of the associated interface.
     * @since 2.0.0
     */
    public final void registerValue(Registered value) throws IllegalArgumentException {
        if (!this.getInterface().isInstance(value)) {
            throw new IllegalArgumentException("Expected an instance of " + this.getInterface().getSimpleName());
        }

        this.consumeAndRegister.accept(value);
    }

    /**
     * Returns whether this environment should be skipped based off of the provided annotation.
     *
     * @param annotation The provided filter annotation.
     *
     * @return Whether this environment should be ignored.
     *
     * @since 2.0.0
     */
    public final boolean isIgnored(IgnoreRegistration annotation) {
        return switch (this) {
            case COMMON -> annotation.common();
            case CLIENT -> annotation.client();
            case SERVER -> annotation.server();
            case GENERATOR -> annotation.generator();
        };
    }

}
