package dev.jaxydog.utility;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import java.util.function.BiFunction;
import java.util.function.Function;

/** Provides an interface for easily manipulating and representing color values */
@NonExtendable
public interface ColorUtil {

	/** Scales the given `min` color upwards towards `max` based on the given `scale` */
	static int scaleUp(int min, int max, double scale) {
		return scaleUp(RGB.from(min), RGB.from(max), scale).getInt();
	}

	/** Scales the given `min` color upwards towards `max` based on the given `scale` */
	static RGB scaleUp(RGB min, RGB max, double scale) {
		final double clamped = Math.max(0D, Math.min(1D, scale));
		final RGB difference = max.apply(min, (mx, mn) -> (short) (mx - mn));
		final RGB scaled = difference.apply(n -> (short) (n * clamped));

		return min.apply(scaled, (n, s) -> (short) (n + s));
	}

	/** Scales the given `max` color downwards towards `min` based on the given `scale` */
	static RGB scaleDown(RGB min, RGB max, double scale) {
		final double clamped = Math.max(0D, Math.min(1D, scale));
		final RGB difference = max.apply(min, (mx, mn) -> (short) (mx - mn));
		final RGB scaled = difference.apply(n -> (short) (n * clamped));

		return max.apply(scaled, (n, s) -> (short) (n - s));
	}

	/** Scales the given `max` color downwards towards `min` based on the given `scale` */
	static int scaleDown(int min, int max, double scale) {
		return scaleUp(RGB.from(min), RGB.from(max), scale).getInt();
	}

	/** Stores an RGB color value */
	final class RGB {

		// The RGB value's color components
		private final short R;
		private final short G;
		private final short B;

		public RGB(short r, short g, short b) {
			this.R = RGB.clamp(r);
			this.G = RGB.clamp(g);
			this.B = RGB.clamp(b);
		}

		/** Clamps the given short to a valid byte value (0-255) */
		private static short clamp(short n) {
			return (short) Math.max(0, Math.min(255, n));
		}

		/** Creates an RGB color value from the provided integer */
		public static RGB from(int color) {
			final short r = (short) (color >> 16 & 0xFF);
			final short g = (short) (color >> 8 & 0xFF);
			final short b = (short) (color & 0xFF);

			return new RGB(r, g, b);
		}

		/** Returns the RGB color's integer representation */
		public int getInt() {
			final int r = ((int) this.getR()) << 16;
			final int g = ((int) this.getG()) << 8;
			final int b = this.getB();

			return r | g | b;
		}

		/** Returns the color's red component */
		public short getR() {
			return this.R;
		}

		/** Returns the color's green component */
		public short getG() {
			return this.G;
		}

		/** Returns the color's blue component */
		public short getB() {
			return this.B;
		}

		/** Applies the given function to the RGB color's color components */
		public RGB apply(Function<Short, Short> f) {
			final short r = f.apply(this.getR());
			final short g = f.apply(this.getG());
			final short b = f.apply(this.getB());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the RGB colors' color components */
		public RGB apply(RGB other, BiFunction<Short, Short, Short> f) {
			final short r = f.apply(this.getR(), other.getR());
			final short g = f.apply(this.getG(), other.getG());
			final short b = f.apply(this.getB(), other.getB());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyR(Function<Short, Short> f) {
			final short r = f.apply(this.getR());

			return new RGB(r, this.getG(), this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyG(Function<Short, Short> f) {
			final short g = f.apply(this.getG());

			return new RGB(this.getR(), g, this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyB(Function<Short, Short> f) {
			final short b = f.apply(this.getB());

			return new RGB(this.getR(), this.getG(), b);
		}

		/** Applies the given function to the RGB colors' R color component */
		public RGB applyR(short other, BiFunction<Short, Short, Short> f) {
			final short r = f.apply(this.getR(), other);

			return new RGB(r, this.getG(), this.getB());
		}

		/** Applies the given function to the RGB colors' R color component */
		public RGB applyG(short other, BiFunction<Short, Short, Short> f) {
			final short g = f.apply(this.getG(), other);

			return new RGB(this.getR(), g, this.getB());
		}

		/** Applies the given function to the RGB colors' R color component */
		public RGB applyB(short other, BiFunction<Short, Short, Short> f) {
			final short b = f.apply(this.getB(), other);

			return new RGB(this.getR(), this.getG(), b);
		}

	}

}
