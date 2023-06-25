package dev.jaxydog.utility;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Provides an interface for easily manipulating and representing color values */
@NonExtendable
public interface ColorUtil {

	/** Scales the given `min` color upwards towards `max` based on the given `scale` */
	public static RGB scaleUp(RGB min, RGB max, double scale) {
		var clamped = Math.max(0.0D, Math.min(1.0D, scale));
		var difference = max.apply(min, (mx, mn) -> (short) (mx - mn));
		var scaled = difference.apply(n -> (short) (n * clamped));

		return min.apply(scaled, (n, s) -> (short) (n + s));
	}

	/** Scales the given `min` color upwards towards `max` based on the given `scale` */
	public static int scaleUp(int min, int max, double scale) {
		return ColorUtil.scaleUp(RGB.from(min), RGB.from(max), scale).getInt();
	}

	/** Scales the given `max` color downwards towards `min` based on the given `scale` */
	public static RGB scaleDown(RGB min, RGB max, double scale) {
		var clamped = Math.max(0.0D, Math.min(1.0D, scale));
		var difference = max.apply(min, (mx, mn) -> (short) (mx - mn));
		var scaled = difference.apply(n -> (short) (n * clamped));

		return max.apply(scaled, (n, s) -> (short) (n - s));
	}

	/** Scales the given `max` color downwards towards `min` based on the given `scale` */
	public static int scaleDown(int min, int max, double scale) {
		return ColorUtil.scaleUp(RGB.from(min), RGB.from(max), scale).getInt();
	}

	/** Stores an RGB color value */
	public static final class RGB {

		// The RGB value's color components
		private final short R;
		private final short G;
		private final short B;

		public RGB(short r, short g, short b) {
			this.R = RGB.clamp(r);
			this.G = RGB.clamp(g);
			this.B = RGB.clamp(b);
		}

		/** Creates an RGB color value from the provided integer */
		public static final RGB from(int color) {
			short r = (short) (color >> 16 & 0xFF);
			short g = (short) (color >> 8 & 0xFF);
			short b = (short) (color & 0xFF);

			return new RGB(r, g, b);
		}

		/** Clamps the given short to a valid byte value (0-255) */
		private static final short clamp(short n) {
			return (short) Math.max(0, Math.min(255, n));
		}

		/** Returns the color's red component */
		public final short getR() {
			return this.R;
		}

		/** Returns the color's green component */
		public final short getG() {
			return this.G;
		}

		/** Returns the color's blue component */
		public final short getB() {
			return this.B;
		}

		/** Returns the RGB color's integer representation */
		public final int getInt() {
			int r = ((int) this.getR()) << 16;
			int g = ((int) this.getG()) << 8;
			int b = (int) this.getB();

			return r | g | b;
		}

		/** Applies the given function to the RGB color's color components */
		public final RGB apply(Function<Short, Short> f) {
			short r = f.apply(this.getR());
			short g = f.apply(this.getG());
			short b = f.apply(this.getB());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the RGB colors' color components */
		public final RGB apply(RGB other, BiFunction<Short, Short, Short> f) {
			short r = f.apply(this.getR(), other.getR());
			short g = f.apply(this.getG(), other.getG());
			short b = f.apply(this.getB(), other.getB());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the RGB color's R color component */
		public final RGB applyR(Function<Short, Short> f) {
			var r = f.apply(this.getR());

			return new RGB(r, this.getG(), this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public final RGB applyG(Function<Short, Short> f) {
			var g = f.apply(this.getG());

			return new RGB(this.getR(), g, this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public final RGB applyB(Function<Short, Short> f) {
			var b = f.apply(this.getB());

			return new RGB(this.getR(), this.getG(), b);
		}

		/** Applies the given function to the RGB colors' R color component */
		public final RGB applyR(short other, BiFunction<Short, Short, Short> f) {
			var r = f.apply(this.getR(), other);

			return new RGB(r, this.getG(), this.getB());
		}

		/** Applies the given function to the RGB colors' R color component */
		public final RGB applyG(short other, BiFunction<Short, Short, Short> f) {
			var g = f.apply(this.getG(), other);

			return new RGB(this.getR(), g, this.getB());
		}

		/** Applies the given function to the RGB colors' R color component */
		public final RGB applyB(short other, BiFunction<Short, Short, Short> f) {
			var b = f.apply(this.getB(), other);

			return new RGB(this.getR(), this.getG(), b);
		}

	}

}
