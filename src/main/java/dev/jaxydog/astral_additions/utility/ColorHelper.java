package dev.jaxydog.astral_additions.utility;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Provides an interface for easily manipulating colors */
public interface ColorHelper {
	/** Scales the given `min` color towards `max`, adding based on the given `scale` */
	public static RGB scaleColorUp(RGB min, RGB max, float scale) {
		return min
			.apply(max, (mn, mx) -> (byte) Math.max(mx - mn, 0)) // subtracts the min from the max color, clamping to 0x00 in case of underflow
			.apply(c -> (byte) ((int) (c * scale) & 0xFF)) // scales the bytes by the provided `scale`
			.apply(min, (mn, r) -> (byte) Math.min(mn + r, 0xFF)); // adds the scaled range to the min color, clamping it to 0xFF in case of overflow
	}

	/** Scales the given `min` color towards `max`, adding based on the given `scale` */
	public static int scaleColorUp(int min, int max, float scale) {
		return ColorHelper.scaleColorUp(RGB.fromInt(min), RGB.fromInt(max), scale).intoInt();
	}

	/** Scales the given `max` color towards `min`, subtracting based on the given `scale` */
	public static RGB scaleColorDown(RGB min, RGB max, float scale) {
		return min
			.apply(max, (mn, mx) -> (byte) Math.max(mx - mn, 0)) // subtracts the min from the max color, clamping to 0x00 in case of underflow
			.apply(c -> (byte) ((int) (c * scale) & 0xFF)) // scales the bytes by the provided `scale`
			.apply(max, (mx, r) -> (byte) Math.max(mx - r, 0)); // subtracts the scaled range from the max color, clamping it to 0x00 in case of underflow
	}

	/** Scales the given `max` color towards `min`, subtracting based on the given `scale` */
	public static int scaleColorDown(int min, int max, float scale) {
		return ColorHelper.scaleColorDown(RGB.fromInt(min), RGB.fromInt(max), scale).intoInt();
	}

	/** Represents an RGB color value */
	public static record RGB(byte r, byte g, byte b) {
		// This just ensures that R, G, and B are all non-null
		public RGB {
			Objects.requireNonNull(this.r());
			Objects.requireNonNull(this.g());
			Objects.requireNonNull(this.b());
		}

		/** Converts the provided integer into an RGB color */
		public static RGB fromInt(int color) {
			// this is just some bit math to extract the contained bytes from the int
			var r = (byte) (color >> 16 & 0xFF);
			var g = (byte) (color >> 8 & 0xFF);
			var b = (byte) (color & 0xFF);

			return new RGB(r, g, b);
		}

		/** Converts the RGB color into an integer */
		public int intoInt() {
			// This is basically just undoing the bit math inside of the `fromInt` method
			// to re-encode the color bytes into a 32-bit integer
			var r = ((int) this.r()) << 16;
			var g = ((int) this.g()) << 8;
			var b = ((int) this.b());

			return r | g | b;
		}

		/** Applies the given function to the color's inner RGB values, returning a new wrapper containing the modified bytes */
		public RGB apply(Function<Byte, Byte> operator) {
			var r = operator.apply(this.r());
			var g = operator.apply(this.g());
			var b = operator.apply(this.b());

			return new RGB(r, g, b);
		}

		/** Applies the given function to the colors' inner RGB values, returning a new wrapper containing the modified bytes */
		public RGB apply(RGB other, BiFunction<Byte, Byte, Byte> operator) {
			var r = operator.apply(this.r(), other.r());
			var g = operator.apply(this.g(), other.g());
			var b = operator.apply(this.b(), other.b());

			return new RGB(r, g, b);
		}
	}
}
