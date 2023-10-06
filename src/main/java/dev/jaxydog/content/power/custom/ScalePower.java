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
    /** The jump scale */
    private final float JUMP;
    /** Whether to reset the entity's scale when the power is lost */
    private final boolean RESET;

    public ScalePower(PowerType<?> type, LivingEntity entity, float width, float height,
            float reach, float motion, float jump, boolean reset) {
        super(type, entity);

        this.WIDTH = width;
        this.HEIGHT = height;
        this.REACH = reach;
        this.MOTION = motion;
        this.JUMP = jump;
        this.RESET = reset;

        this.setTicking(true);
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

    /** Returns the entity's jump scale API */
    public static ScaleData getJumpScale(LivingEntity entity) {
        return ScaleData.Builder.create().entity(entity).type(ScaleTypes.JUMP_HEIGHT).build();
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

    /** Returns the powers's jump scale */
    public float getJumpScale() {
        return this.JUMP;
    }

    /** Returns whether the entity's scale should be reset when the power is lost */
    public boolean isResetOnLost() {
        return this.RESET;
    }

    /** Sets the entity's scale to the given value, returning early if the scale is unchanged. */
    private void setScale(ScaleData data, float scale) {
        this.setScale(data, scale, false);
    }

    /** Sets the entity's scale to the given value, returning early if the scale is unchanged. */
    private void setScale(ScaleData data, float scale, boolean force) {
        if (data.getScale() == scale && !force) {
            return;
        }

        data.setScale(scale);
    }

    /** Sets the entity's scale to match the power's values */
    private void setScale() {
        this.setScale(ScalePower.getWidthScale(this.entity), this.getWidthScale());
        this.setScale(ScalePower.getHeightScale(this.entity), this.getHeightScale());
        this.setScale(ScalePower.getReachScale(this.entity), this.getReachScale());
        this.setScale(ScalePower.getMotionScale(this.entity), this.getMotionScale());
        this.setScale(ScalePower.getJumpScale(this.entity), this.getJumpScale());
    }

    /** Resets the entity's scale to the default values */
    private void resetScale() {
        this.setScale(ScalePower.getWidthScale(this.entity), 1.0F);
        this.setScale(ScalePower.getHeightScale(this.entity), 1.0F);
        this.setScale(ScalePower.getReachScale(this.entity), 1.0F);
        this.setScale(ScalePower.getMotionScale(this.entity), 1.0F);
        this.setScale(ScalePower.getJumpScale(this.entity), 1.0F);
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
