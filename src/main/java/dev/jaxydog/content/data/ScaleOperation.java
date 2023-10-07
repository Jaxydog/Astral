package dev.jaxydog.content.data;

import virtuoel.pehkui.api.ScaleData;

/** Represents a possible scaling operation */
public enum ScaleOperation {

    /** Multiplies the scale */
    MULTIPLICATIVE,
    /** Adds to the scale */
    ADDITIVE;

    /** Returns the scale operation with the given name */
    public static ScaleOperation from(String name) {
        for (ScaleOperation operation : ScaleOperation.values()) {
            if (operation.getName() == name.toLowerCase()) {
                return operation;
            }
        }

        return ScaleOperation.MULTIPLICATIVE;
    }

    /** Returns the operation's name */
    public String getName() {
        return this.toString().toLowerCase();
    }

    /** Returns the target scale for the given data */
    public float getTarget(ScaleData data, float value) {
        return switch (this) {
            case ADDITIVE -> data.getInitialScale() + value;
            case MULTIPLICATIVE -> value;
        };
    }

    /** Sets the scale of the given data */
    public void setScale(ScaleData data, float value) {
        final float target = this.getTarget(data, value);

        if (data.getScale() != target) {
            data.setScale(target);
        }
    }

    /** Sets the scale of the given data */
    public void resetScale(ScaleData data) {
        if (data.getScale() != data.getInitialScale()) {
            data.resetScale();
        }
    }

}
