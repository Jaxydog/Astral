package dev.jaxydog.astral.content.power.custom;

import dev.jaxydog.astral.content.power.CustomPower;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

/** The scale power */
@SuppressWarnings("removal") // To be removed in tandem.
@Deprecated(since = "1.7.0", forRemoval = true)
public class ScalePower extends CustomPower {

    /** The width scale */
    private final float WIDTH;
    /** The height scale */
    private final float HEIGHT;
    /** The reach scale */
    private final float REACH;
    /** The motion scale */
    private final float MOTION;
    /** The jump scale */
    private final float JUMP;
    /** Whether to reset the entity's scale when the power is lost */
    private final boolean RESET;
    /** The scale operation */
    private final dev.jaxydog.astral.content.data.ScaleOperation OPERATION;

    public ScalePower(
        PowerType<?> type,
        LivingEntity entity,
        float width,
        float height,
        float reach,
        float motion,
        float jump,
        boolean reset,
        dev.jaxydog.astral.content.data.ScaleOperation operation
    ) {
        super(type, entity);

        this.WIDTH = width;
        this.HEIGHT = height;
        this.REACH = reach;
        this.MOTION = motion;
        this.JUMP = jump;
        this.RESET = reset;
        this.OPERATION = operation;

        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (this.isActive()) {
            this.setScale();
        } else if (this.isResetOnLost()) {
            this.resetScale();
        }

        super.tick();
    }

    /** Sets the entity's scale to match the power's values */
    private void setScale() {
        this.getOperation().setScale(this.getWidthScaleData(), this.getWidthScale());
        this.getOperation().setScale(this.getHeightScaleData(), this.getHeightScale());
        this.getOperation().setScale(this.getReachScaleData(), this.getReachScale());
        this.getOperation().setScale(this.getMotionScaleData(), this.getMotionScale());
        this.getOperation().setScale(this.getJumpScaleData(), this.getJumpScale());
    }

    /** Returns whether the entity's scale should be reset when the power is lost */
    public boolean isResetOnLost() {
        return this.RESET;
    }

    /** Resets the entity's scale to the default values */
    private void resetScale() {
        this.getOperation().resetScale(this.getWidthScaleData());
        this.getOperation().resetScale(this.getHeightScaleData());
        this.getOperation().resetScale(this.getReachScaleData());
        this.getOperation().resetScale(this.getMotionScaleData());
        this.getOperation().resetScale(this.getJumpScaleData());
    }

    /** Returns the scale operation */
    public dev.jaxydog.astral.content.data.ScaleOperation getOperation() {
        return this.OPERATION;
    }

    /** Returns the entity's width scale API */
    public ScaleData getWidthScaleData() {
        return ScaleData.Builder.create().entity(this.entity).type(ScaleTypes.WIDTH).build();
    }

    /** Returns the power's width scale */
    public float getWidthScale() {
        return this.WIDTH;
    }

    /** Returns the entity's height scale API */
    public ScaleData getHeightScaleData() {
        return ScaleData.Builder.create().entity(this.entity).type(ScaleTypes.HEIGHT).build();
    }

    /** Returns the power's height scale */
    public float getHeightScale() {
        return this.HEIGHT;
    }

    /** Returns the entity's reach scale API */
    public ScaleData getReachScaleData() {
        return ScaleData.Builder.create().entity(this.entity).type(ScaleTypes.REACH).build();
    }

    /** Returns the power's reach scale */
    public float getReachScale() {
        return this.REACH;
    }

    /** Returns the entity's motion scale API */
    public ScaleData getMotionScaleData() {
        return ScaleData.Builder.create().entity(this.entity).type(ScaleTypes.MOTION).build();
    }

    /** Returns the power's motion scale */
    public float getMotionScale() {
        return this.MOTION;
    }

    /** Returns the entity's jump scale API */
    public ScaleData getJumpScaleData() {
        return ScaleData.Builder.create().entity(this.entity).type(ScaleTypes.JUMP_HEIGHT).build();
    }

    /** Returns the power's jump scale */
    public float getJumpScale() {
        return this.JUMP;
    }

    @Override
    public void onGained() {
        if (this.isActive()) this.setScale();

        super.onGained();
    }

    @Override
    public void onLost() {
        if (this.isResetOnLost()) this.resetScale();

        super.onLost();
    }

    @Override
    public void onAdded() {
        if (this.isActive()) this.setScale();

        super.onAdded();
    }

    @Override
    public void onRemoved() {
        if (this.isResetOnLost()) this.resetScale();

        super.onRemoved();
    }

}
