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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as intentionally ignored during the automatic registration process.
 * <p>
 * By default, all environments are ignored. The ignored environments can be configured via the annotation's arguments.
 *
 * @author Jaxydog
 * @see Registered
 * @see Environment
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreRegistration {

    /**
     * Configures whether registration within the common environment should be ignored.
     * <p>
     * Note that this is distinctly different from {@link #client()} and {@link #server()}.
     * <p>
     * This field is {@code true} by default.
     *
     * @return Whether the common environment is ignored.
     *
     * @since 2.0.0
     */
    boolean common() default true;

    /**
     * Configures whether registration within the client environment should be ignored.
     * <p>
     * This field is {@code true} by default.
     *
     * @return Whether the client environment is ignored.
     *
     * @since 2.0.0
     */
    boolean client() default true;

    /**
     * Configures whether registration within the server environment should be ignored.
     * <p>
     * This field is {@code true} by default.
     *
     * @return Whether the server environment is ignored.
     *
     * @since 2.0.0
     */
    boolean server() default true;

    /**
     * Configures whether registration within the data generation environment should be ignored.
     * <p>
     * This field is {@code true} by default.
     *
     * @return Whether the generation environment is ignored.
     *
     * @since 2.0.0
     */
    boolean generator() default true;

}
