package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomPower;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

/** The scale power */
public class ScalePower extends CustomPower {

	/** The width scale */
	private final float WIDTH;
	/** The height scale */
	private final float HEIGHT;
	/** The reach scale */
	private final float REACH;
	/** The motion scale */
	private final float MOTION;
	/** Whether to reset the entity's scale when the power is lost */
	private final boolean RESET;

	public ScalePower(PowerType<?> type, LivingEntity entity, float width, float height,
			float reach, float motion, boolean reset) {
		super(type, entity);
		this.WIDTH = width;
		this.HEIGHT = height;
		this.REACH = reach;
		this.MOTION = motion;
		this.RESET = reset;
	}

	/** Returns the entity's width scale API */
	public static ScaleData getWidthScale(LivingEntity entity) {
		return ScaleData.Builder.create().entity(entity).type(ScaleTypes.WIDTH).build();
	}

	/** Returns the entity's height scale API */
	public static ScaleData getHeightScale(LivingEntity entity) {
		return ScaleData.Builder.create().entity(entity).type(ScaleTypes.HEIGHT).build();
	}

	/** Returns the entity's reach scale API */
	public static ScaleData getReachScale(LivingEntity entity) {
		return ScaleData.Builder.create().entity(entity).type(ScaleTypes.REACH).build();
	}

	/** Returns the entity's motion scale API */
	public static ScaleData getMotionScale(LivingEntity entity) {
		return ScaleData.Builder.create().entity(entity).type(ScaleTypes.MOTION).build();
	}

	/** Returns the powers's width scale */
	public float getWidthScale() {
		return this.WIDTH;
	}

	/** Returns the powers's height scale */
	public float getHeightScale() {
		return this.HEIGHT;
	}

	/** Returns the powers's reach scale */
	public float getReachScale() {
		return this.REACH;
	}

	/** Returns the powers's motion scale */
	public float getMotionScale() {
		return this.MOTION;
	}

	/** Returns whether the entity's scale should be reset when the power is lost */
	public boolean isResetOnLost() {
		return this.RESET;
	}

	/** Sets the entity's scale to match the power's values */
	private void setScale() {
		ScalePower.getWidthScale(this.entity).setScale(this.getWidthScale());
		ScalePower.getHeightScale(this.entity).setScale(this.getHeightScale());
		ScalePower.getReachScale(this.entity).setScale(this.getReachScale());
		ScalePower.getMotionScale(this.entity).setScale(this.getMotionScale());
	}

	/** Resets the entity's scale to the default values */
	private void resetScale() {
		ScalePower.getWidthScale(this.entity).setScale(1.0F);
		ScalePower.getHeightScale(this.entity).setScale(1.0F);
		ScalePower.getReachScale(this.entity).setScale(1.0F);
		ScalePower.getMotionScale(this.entity).setScale(1.0F);
	}

	@Override
	public void tick() {
		if (this.isActive()) {
			this.setScale();
		}

		super.tick();
	}

	@Override
	public void onAdded() {
		if (this.isActive()) {
			this.setScale();
		}

		super.onAdded();
	}

	@Override
	public void onGained() {
		if (this.isActive()) {
			this.setScale();
		}

		super.onGained();
	}

	@Override
	public void onLost() {
		if (this.isResetOnLost()) {
			this.resetScale();
		}

		super.onLost();
	}

	@Override
	public void onRemoved() {
		if (this.isResetOnLost()) {
			this.resetScale();
		}

		super.onRemoved();
	}

}
