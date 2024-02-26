package dev.jaxydog.astral.content.data;

import virtuoel.pehkui.api.ScaleData;

/** Represents a possible scaling operation */
@Deprecated(since = "1.7.0", forRemoval = true)
public enum ScaleOperation {

    /** Multiplies the scale */
    MULTIPLICATIVE,
    /** Adds to the scale */
    ADDITIVE;

    /** Returns the scale operation with the given name */
    public static ScaleOperation from(String name) {
        final String lower = name.toLowerCase();

        for (final ScaleOperation operation : values()) {
            if (operation.getName().equals(lower)) return operation;
        }

        return MULTIPLICATIVE;
    }

    /** Returns the operation's name */
    public String getName() {
        return this.toString().toLowerCase();
    }

    /** Sets the scale of the given data */
    public void setScale(ScaleData data, float value) {
        data.setScale(this.getTarget(value));
    }

    /** Returns the target scale for the given data */
    public float getTarget(float value) {
        return switch (this) {
            case ADDITIVE -> 1F + value;
            case MULTIPLICATIVE -> value;
        };
    }

    /** Sets the scale of the given data */
    public void resetScale(ScaleData data) {
        data.resetScale();
    }

}
