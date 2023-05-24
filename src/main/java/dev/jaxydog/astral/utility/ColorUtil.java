package dev.jaxydog.astral.utility;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.joml.Matrix3d;

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
		return ColorUtil.scaleUp(RGB.from(min), RGB.from(max), scale).intoInt();
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
		return ColorUtil.scaleUp(RGB.from(min), RGB.from(max), scale).intoInt();
	}

	/** Stores an RGB color value */
	public static class RGB {

		/** Matrix used to rotate the hue of an RGB value */
		private static Matrix3d HUE_MATRIX = new Matrix3d(1, 0, 0, 0, 1, 0, 0, 0, 1);

		// the color components of the RGB value
		private final short R;
		private final short G;
		private final short B;

		public RGB(short r, short g, short b) {
			this.R = RGB.clamp(r);
			this.G = RGB.clamp(g);
			this.B = RGB.clamp(b);
		}

		/** Clamps the given value to the acceptable color component range */
		private static short clamp(short n) {
			return (short) Math.max(0, Math.min(255, n));
		}

		/** Creates an RGB color value from the provided integer */
		public static RGB from(int color) {
			var r = (short) (color >> 16 & 0xFF);
			var g = (short) (color >> 8 & 0xFF);
			var b = (short) (color & 0xFF);

			return new RGB(r, g, b);
		}

		/** Returns an integer representation of the RGB color value */
		public int intoInt() {
			var r = ((int) this.getR()) << 16;
			var g = ((int) this.getG()) << 8;
			var b = ((int) this.getB());

			return r | g | b;
		}

		/** Returns the color's R component */
		public short getR() {
			return this.R;
		}

		/** Returns the color's G component */
		public short getG() {
			return this.G;
		}

		/** Returns the color's B component */
		public short getB() {
			return this.B;
		}

		/** Applies the given function to the RGB color's color components */
		public RGB apply(Function<Short, Short> f) {
			var r = f.apply(this.getR());
			var g = f.apply(this.getG());
			var b = f.apply(this.getB());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the RGB colors' color components */
		public RGB apply(RGB other, BiFunction<Short, Short, Short> f) {
			var r = f.apply(this.getR(), other.getR());
			var g = f.apply(this.getG(), other.getG());
			var b = f.apply(this.getB(), other.getB());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyR(Function<Short, Short> f) {
			var r = f.apply(this.getR());

			return new RGB(r, this.getG(), this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyG(Function<Short, Short> f) {
			var g = f.apply(this.getG());

			return new RGB(this.getR(), g, this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyB(Function<Short, Short> f) {
			var b = f.apply(this.getB());

			return new RGB(this.getR(), this.getG(), b);
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyR(short other, BiFunction<Short, Short, Short> f) {
			var r = f.apply(this.getR(), other);

			return new RGB(r, this.getG(), this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyG(short other, BiFunction<Short, Short, Short> f) {
			var g = f.apply(this.getG(), other);

			return new RGB(this.getR(), g, this.getB());
		}

		/** Applies the given function to the RGB color's R color component */
		public RGB applyB(short other, BiFunction<Short, Short, Short> f) {
			var b = f.apply(this.getB(), other);

			return new RGB(this.getR(), this.getG(), b);
		}

		/** Rotates the hue of the RGB color value by the given amount */
		public RGB rotate(double degrees) {
			var radians = (degrees * Math.PI) / 180.0D;
			var sin = Math.sin(radians);
			var cos = Math.cos(radians);
			var matrix = RGB.HUE_MATRIX;

			// some weird af matrix math to calculate the color transforms
			matrix.set(0, 0, cos + (1.0D - cos) / 3.0D);
			matrix.set(0, 1, 1.0D / 3.0D * (1.0D - cos) - Math.sqrt(1.0D / 3.0D) * sin);
			matrix.set(0, 2, 1.0D / 3.0D * (1.0D - cos) + Math.sqrt(1.0D / 3.0D) * sin);
			matrix.set(1, 0, 1.0D / 3.0D * (1.0D - cos) + Math.sqrt(1.0D / 3.0D) * sin);
			matrix.set(1, 1, cos + 1.0D / 3.0D * (1.0D - cos));
			matrix.set(1, 2, 1.0D / 3.0D * (1.0D - cos) - Math.sqrt(1.0D / 3.0D) * sin);
			matrix.set(2, 0, 1.0D / 3.0D * (1.0D - cos) - Math.sqrt(1.0D / 3.0D) * sin);
			matrix.set(2, 1, 1.0D / 3.0D * (1.0D - cos) + Math.sqrt(1.0D / 3.0D) * sin);
			matrix.set(2, 2, cos + 1.0D / 3.0D * (1.0D - cos));

			var r = this.getR() * matrix.get(0, 0) + this.getG() * matrix.get(0, 1) + this.getB() * matrix.get(0, 2);
			var g = this.getR() * matrix.get(1, 0) + this.getG() * matrix.get(1, 1) + this.getB() * matrix.get(1, 2);
			var b = this.getR() * matrix.get(2, 0) + this.getG() * matrix.get(2, 1) + this.getB() * matrix.get(2, 2);

			return new RGB((short) r, (short) g, (short) b);
		}
	}
}
