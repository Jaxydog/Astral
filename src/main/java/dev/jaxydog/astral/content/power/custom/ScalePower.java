package dev.jaxydog.astral.content.power.custom;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

/** Implements a power that allows the modification of an origin's scale */
public class ScalePower extends Power {

	// Stored scale values
	private final float WIDTH;
	private final float HEIGHT;
	private final float REACH;
	private final float MOTION;
	private final float JUMP;
	private final boolean RESET;

	public ScalePower(
		PowerType<?> type,
		LivingEntity entity,
		float width,
		float height,
		float reach,
		float motion,
		float jump,
		boolean reset
	) {
		super(type, entity);
		this.WIDTH = width;
		this.HEIGHT = height;
		this.REACH = reach;
		this.MOTION = motion;
		this.JUMP = jump;
		this.RESET = reset;

		this.setTicking();
	}

	/** Returns the power's set width scale */
	public float getWidthScale() {
		return this.WIDTH;
	}

	/** Returns the power's set height scale */
	public float getHeightScale() {
		return this.HEIGHT;
	}

	/** Returns the power's set reach scale */
	public float getReachScale() {
		return this.REACH;
	}

	/** Returns the power's set motion scale */
	public float getMotionScale() {
		return this.MOTION;
	}

	/** Returns the power's set jump scale */
	public float getJumpScale() {
		return this.JUMP;
	}

	/** Returns whether the entity's scale is reset when the power is lost */
	public boolean isResetOnLost() {
		return this.RESET;
	}

	@Override
	public void onAdded() {
		if (this.isActive()) this.forceScale();
		super.onAdded();
	}

	@Override
	public void onGained() {
		if (this.isActive()) this.forceScale();
		super.onGained();
	}

	@Override
	public void onLost() {
		if (this.isResetOnLost()) this.resetScale();

		super.onLost();
	}

	@Override
	public void onRemoved() {
		if (this.isResetOnLost()) this.resetScale();

		super.onRemoved();
	}

	@Override
	public void onRespawn() {
		if (this.isActive()) {
			this.forceScale();
		} else if (this.isResetOnLost()) {
			this.resetScale();
		}

		super.onRespawn();
	}

	@Override
	public void tick() {
		if (this.isActive()) {
			this.forceScale();
		} else if (this.isResetOnLost()) {
			this.resetScale();
		}

		super.tick();
	}

	/** Returns an interface used to manipulate the entity's requested scale */
	private ScaleData getScaleFor(ScaleType type) {
		return ScaleData.Builder.create().entity(this.entity).type(type).build();
	}

	/** Sets the entity's scale to the power's set values */
	private void forceScale() {
		this.getScaleFor(ScaleTypes.WIDTH).setScale(this.getWidthScale());
		this.getScaleFor(ScaleTypes.HEIGHT).setScale(this.getHeightScale());
		this.getScaleFor(ScaleTypes.REACH).setScale(this.getReachScale());
		this.getScaleFor(ScaleTypes.MOTION).setScale(this.getMotionScale());
		this.getScaleFor(ScaleTypes.JUMP_HEIGHT).setScale(this.getJumpScale());
	}

	/** Resets the entity's scale back to default */
	private void resetScale() {
		this.getScaleFor(ScaleTypes.WIDTH).setScale(1.0F);
		this.getScaleFor(ScaleTypes.HEIGHT).setScale(1.0F);
		this.getScaleFor(ScaleTypes.REACH).setScale(1.0F);
		this.getScaleFor(ScaleTypes.MOTION).setScale(1.0F);
		this.getScaleFor(ScaleTypes.JUMP_HEIGHT).setScale(1.0F);
	}
}
