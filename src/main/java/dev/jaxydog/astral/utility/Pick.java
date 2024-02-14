/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * Contains a value that is either of type {@code <A>} or {@code <B>}.
 *
 * @param <A> The first possible type.
 * @param <B> The second possible type.
 *
 * @author Jaxydog
 */
public sealed abstract class Pick<A, B> permits Pick.PickA, Pick.PickB {

    /**
     * Returns a new instance containing a value of type {@code <A>}.
     *
     * @param value The value.
     * @param <A> The value's type.
     * @param <B> The type assigned to the value that is not present.
     *
     * @return A new instance.
     */
    @Contract(value = "_ -> new", pure = true)
    public static <A, B> @NotNull Pick<A, B> a(A value) {
        return new PickA<>(value);
    }

    /**
     * Returns a new instance containing a value of type {@code <B>}.
     *
     * @param value The value.
     * @param <A> The type assigned to the value that is not present.
     * @param <B> The value's type.
     *
     * @return A new instance.
     */
    @Contract(value = "_ -> new", pure = true)
    public static <A, B> @NotNull Pick<A, B> b(B value) {
        return new PickB<>(value);
    }

    /**
     * Returns whether the pair holds the type specified by {@code <A>}.
     *
     * @return If this holds a value of type {@code <A>}.
     */
    public abstract boolean isA();

    /**
     * Returns whether the pair holds the type specified by {@code <B>}.
     *
     * @return If this holds a value of type {@code <B>}.
     */
    public abstract boolean isB();

    /**
     * Returns the value assuming that it is of type {@code <A>}.
     *
     * @return A value of type {@code <A>}.
     *
     * @throws NoSuchElementException If the value is of type {@code <B>}.
     */
    public abstract A getA() throws NoSuchElementException;

    /**
     * Returns the value assuming that it is of type {@code <B>}.
     *
     * @return A value of type {@code <B>}.
     *
     * @throws NoSuchElementException If the value is of type {@code <A>}.
     */
    public abstract B getB() throws NoSuchElementException;

    /**
     * Contains a value that is either of {@code <A>}.
     *
     * @param <A> The type of the stored field.
     * @param <B> The unused type.
     *
     * @author Jaxydog
     */
    private static final class PickA<A, B> extends Pick<A, B> {

        /** The stored value. */
        private final A value;

        private PickA(A value) {
            this.value = value;
        }

        @Override
        @Contract(pure = true)
        public boolean isA() {
            return true;
        }

        @Override
        @Contract(pure = true)
        public boolean isB() {
            return false;
        }

        @Override
        @Contract(pure = true)
        public A getA() throws NoSuchElementException {
            return value;
        }

        @Override
        @Contract(value = " -> fail", pure = true)
        public B getB() throws NoSuchElementException {
            throw new NoSuchElementException("The value is of type <A>");
        }

    }

    /**
     * Contains a value that is either of {@code <B>}.
     *
     * @param <A> The unused type.
     * @param <B> The type of the stored field.
     *
     * @author Jaxydog
     */
    private static final class PickB<A, B> extends Pick<A, B> {

        /** The stored value. */
        private final B value;

        private PickB(B value) {
            this.value = value;
        }

        @Override
        @Contract(pure = true)
        public boolean isA() {
            return false;
        }

        @Override
        @Contract(pure = true)
        public boolean isB() {
            return true;
        }

        @Override
        @Contract(value = " -> fail", pure = true)
        public A getA() throws NoSuchElementException {
            throw new NoSuchElementException("The value is of type <B>");
        }

        @Override
        @Contract(pure = true)
        public B getB() throws NoSuchElementException {
            return value;
        }

    }

}
