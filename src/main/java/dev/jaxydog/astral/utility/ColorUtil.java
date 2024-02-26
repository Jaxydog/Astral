package dev.jaxydog.astral.utility;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Provides an interface for easily manipulating and representing color values */
@SuppressWarnings("unused")
@NonExtendable
public interface ColorUtil {

    /** Performs a linear transition between two colors */
    static Rgb transition(Rgb start, Rgb end, double delta) {
        // Scary math, just linearly interpolates between the two values per color component.
        final int r = (int) ((end.r() - start.r()) * delta);
        final int g = (int) ((end.g() - start.g()) * delta);
        final int b = (int) ((end.b() - start.b()) * delta);

        // Adds the computed transition onto the starting color.
        return new Rgb(start.r() + r, start.g() + g, start.b() + b);
    }

    /** Performs a linear transition between two colors */
    static int transition(int start, int end, double delta) {
        // Just converts the given integers into RGB colors.
        return transition(new Rgb(start), new Rgb(end), delta).asInt();
    }

    /** Stores an RGB color value. */
    record Rgb(int asInt) {

        public Rgb {
            // Ensure that the given integer does not have a set alpha value.
            asInt &= 0x00FFFFFF;
        }

        public Rgb(int r, int g, int b) {
            // Shifts the R, G, and B channel's bits into the correct places, then combines into a single int.
            this(((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
        }

        public Rgb(float r, float g, float b) {
            // Converts the given floats from a 0-1 range into a 0-255 range.
            this((int) (r * 255F), (int) (g * 255F), (int) (b * 255F));
        }

        /** Returns the color's red component */
        public int r() {
            // Unpacks the color channel from the internal integer.
            return (this.asInt() >> 16) & 0xFF;
        }

        /** Returns the color's green component */
        public int g() {
            // Unpacks the color channel from the internal integer.
            return (this.asInt() >> 8) & 0xFF;
        }

        /** Returns the color's blue component */
        public int b() {
            // Unpacks the color channel from the internal integer.
            return this.asInt() & 0xFF;
        }

        /** Returns the color's hue */
        public float hue() {
            return Color.RGBtoHSB(this.r(), this.g(), this.b(), null)[0];
        }

        /** Returns the color's saturation */
        public float saturation() {
            return Color.RGBtoHSB(this.r(), this.g(), this.b(), null)[1];
        }

        /** Returns the color's brightness */
        public float brightness() {
            return Color.RGBtoHSB(this.r(), this.g(), this.b(), null)[2];
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

        public Rgb withHue(float hue) {
            final float[] hsb = Color.RGBtoHSB(this.r(), this.g(), this.b(), null);
            final float clamped = MathHelper.clamp(hue, 0F, 1F);

            return new Rgb(Color.HSBtoRGB(clamped, hsb[1], hsb[2]));
        }

        public Rgb withSaturation(float saturation) {
            final float[] hsb = Color.RGBtoHSB(this.r(), this.g(), this.b(), null);
            final float clamped = MathHelper.clamp(saturation, 0F, 1F);

            return new Rgb(Color.HSBtoRGB(hsb[0], clamped, hsb[2]));
        }

        public Rgb withBrightness(float brightness) {
            final float[] hsb = Color.RGBtoHSB(this.r(), this.g(), this.b(), null);
            final float clamped = MathHelper.clamp(brightness, 0F, 1F);

            return new Rgb(Color.HSBtoRGB(hsb[0], hsb[1], clamped));
        }

    }

}
