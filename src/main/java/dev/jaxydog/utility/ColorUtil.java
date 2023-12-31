package dev.jaxydog.utility;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import java.util.function.BiFunction;
import java.util.function.Function;

/** Provides an interface for easily manipulating and representing color values */
@NonExtendable
public interface ColorUtil {

	/** Performs a linear transition between two colors */
	static Rgb transition(Rgb start, Rgb end, double delta) {
		final int r = (int) ((end.r() - start.r()) * delta);
		final int g = (int) ((end.g() - start.g()) * delta);
		final int b = (int) ((end.b() - start.b()) * delta);

		return new Rgb(start.r() + r, start.g() + g, start.b() + b);
	}

	/** Performs a linear transition between two colors */
	static int transition(int start, int end, double delta) {
		return transition(new Rgb(start), new Rgb(end), delta).asInt();
	}

	/** Stores an RGB color value. */
	record Rgb(int asInt) {

		public Rgb(int r, int g, int b) {
			this(((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
		}

		/** Returns the color's red component */
		public int r() {
			return (this.asInt() >> 16) & 0xFF;
		}

		/** Returns the color's green component */
		public int g() {
			return (this.asInt() >> 8) & 0xFF;
		}

		/** Returns the color's blue component */
		public int b() {
			return this.asInt() & 0xFF;
		}

		/** Maps each component using the given closure. */
		public Rgb map(Function<Integer, Integer> f) {
			final int r = f.apply(this.r());
			final int g = f.apply(this.g());
			final int b = f.apply(this.b());

			return new Rgb(r, g, b);
		}

		/** Mixes each component using the given closure. */
		public Rgb mix(Rgb other, BiFunction<Integer, Integer, Integer> f) {
			final int r = f.apply(this.r(), other.r());
			final int g = f.apply(this.g(), other.g());
			final int b = f.apply(this.b(), other.b());

			return new Rgb(r, g, b);
		}

	}

}
