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
		for (final ScaleOperation operation : ScaleOperation.values()) {
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
			case ADDITIVE -> 1.0F + value;
			case MULTIPLICATIVE -> value;
		};
	}

	/** Sets the scale of the given data */
	public void setScale(ScaleData data, float value) {
		data.setScale(this.getTarget(data, value));
	}

	/** Sets the scale of the given data */
	public void resetScale(ScaleData data) {
		data.resetScale();
	}

}
