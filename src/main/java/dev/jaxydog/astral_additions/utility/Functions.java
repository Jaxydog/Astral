package dev.jaxydog.astral_additions.utility;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Contains non-standard function interfaces for when things get really weird */
@NonExtendable
public interface Functions {
	@FunctionalInterface
	public static interface TriFunction<A, B, C, R> {
		public R apply(A a, B b, C c);
	}

	@FunctionalInterface
	public static interface QuadFunction<A, B, C, D, R> {
		public R apply(A a, B b, C c, D d);
	}

	@FunctionalInterface
	public static interface QuintFunction<A, B, C, D, E, R> {
		public R apply(A a, B b, C c, D d, E e);
	}

	@FunctionalInterface
	public static interface TriConsumer<A, B, C> {
		public void accept(A a, B b, C c);
	}

	@FunctionalInterface
	public static interface QuadConsumer<A, B, C, D> {
		public void accept(A a, B b, C c, D d);
	}

	@FunctionalInterface
	public static interface QuintConsumer<A, B, C, D, E> {
		public void accept(A a, B b, C c, D d, E e);
	}
}
