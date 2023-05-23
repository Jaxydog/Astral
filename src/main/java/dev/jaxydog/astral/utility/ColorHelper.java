package dev.jaxydog.astral.utility;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Provides an interface for easily manipulating colors */
@NonExtendable
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
		return ColorHelper.scaleColorUp(RGB.from(min), RGB.from(max), scale).intoInt();
	}

	/** Scales the given `min` color towards `max`, adding based on the given `scale` */
	public static HSL scaleColorUp(HSL min, HSL max, float scale) {
		return ColorHelper.scaleColorUp(RGB.from(min), RGB.from(max), scale).intoHSL();
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
		return ColorHelper.scaleColorDown(RGB.from(min), RGB.from(max), scale).intoInt();
	}

	/** Scales the given `max` color towards `min`, subtracting based on the given `scale` */
	public static HSL scaleColorDown(HSL min, HSL max, float scale) {
		return ColorHelper.scaleColorDown(RGB.from(min), RGB.from(max), scale).intoHSL();
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
		public static RGB from(int color) {
			// this is just some bit math to extract the contained bytes from the int
			var r = (byte) (color >> 16 & 0xFF);
			var g = (byte) (color >> 8 & 0xFF);
			var b = (byte) (color & 0xFF);

			return new RGB(r, g, b);
		}

		/** Converts the provided HSL color into an RGB color */
		public static RGB from(HSL color) {
			return color.intoRGB();
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

		/** Converts the RGB color into an HSL color */
		public HSL intoHSL() {
			var max = (float) this.max() / 255.0F;
			var min = (float) this.min() / 255.0F;
			float h, s, l = (max + min) / 2.0F;

			if (max == min) {
				h = 0.0F;
				s = 0.0F;
			} else {
				var d = max - min;
				var div = l > 0.5F ? (2.0F - max - min) : (max + min);

				s = d / div;

				var maxComponent = this.max();

				if (maxComponent == this.r()) {
					h = (this.g() - this.b()) / d + (this.g() < this.b() ? 6.0F : 0.0F);
				} else if (maxComponent == this.g()) {
					h = (this.b() - this.r()) / d + 2.0F;
				} else {
					h = (this.r() - this.g()) / d + 4.0F;
				}

				h /= 6.0;
			}

			return new HSL(h, s, l);
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

		/** Returns the color's highest color component */
		public byte max() {
			return (byte) Math.max(this.r(), Math.max(this.g(), this.b()));
		}

		/** Returns the color's lowest color component */
		public byte min() {
			return (byte) Math.min(this.r(), Math.min(this.g(), this.b()));
		}
	}

	/** Represents an HSL color value */
	public static record HSL(float h, float s, float l) {
		// This just ensures that H, S, and L are all non-null
		public HSL {
			Objects.requireNonNull(this.h());
			Objects.requireNonNull(this.s());
			Objects.requireNonNull(l);
		}

		/** Converts the given values into an RGB component */
		private static float hueToRGBComponent(float p, float q, float t) {
			if (t < 0.0F) t += 1.0F; else if (t > 1.0F) t -= 1.0F;

			if (t < 1.0F / 6.0F) return p + (q - p) * 6.0F * t;
			if (t < 1.0F / 2.0F) return q;
			if (t < 2.0F / 3.0F) return p + (q - p) * 6.0F * t;

			return p;
		}

		/** Converts the provided integer into an HSL color */
		public static HSL from(int color) {
			return RGB.from(color).intoHSL();
		}

		/** Converts the provided RGB color into an HSL color */
		public static HSL from(RGB color) {
			return color.intoHSL();
		}

		/** Converts the HSL color into an integer */
		public int intoInt() {
			return this.intoRGB().intoInt();
		}

		/** Converts the HSL color into an RGB color */
		public RGB intoRGB() {
			float r, g, b;
			var h = this.h();
			var s = this.s();
			var l = this.l();

			if (this.s() == 0.0F) {
				r = l;
				g = l;
				b = l;
			} else {
				var q = (l < 0.5F) ? (l * (1.0F + s)) : (l + s - l * s);
				var p = 2.0F * l - q;

				r = HSL.hueToRGBComponent(p, q, h + (1.0F / 3.0F));
				g = HSL.hueToRGBComponent(p, q, h);
				b = HSL.hueToRGBComponent(p, q, h - (1.0F / 3.0F));
			}

			return new RGB((byte) (r * 0xFF), (byte) (g * 0xFF), (byte) (b * 0xFF));
		}

		/** Applies the given function to the color, returning a new HSL color containing the modified values */
		public HSL apply(Function<HSL, HSL> operator) {
			var hsl = operator.apply(new HSL(this.h(), this.s(), this.l()));

			return hsl;
		}

		/** Applies the given function to the colors, returning a new HSL color containing the modified values */
		public HSL apply(HSL other, BiFunction<HSL, HSL, HSL> operator) {
			var hsl = operator.apply(new HSL(this.h(), this.s(), this.l()), other);

			return hsl;
		}
	}
}
