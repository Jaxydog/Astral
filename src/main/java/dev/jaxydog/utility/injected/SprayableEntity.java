package dev.jaxydog.utility.injected;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface SprayableEntity {

    void astral$setSprayed(@Nullable LivingEntity source, int ticks, boolean initialSpray);

    default void astral$setSprayed(@Nullable LivingEntity source, int ticks) {
        this.astral$setSprayed(source, ticks, true);
    }

    default void astral$setUnsprayed() {
        this.astral$setSprayed(null, 0, false);
    }

    @Nullable LivingEntity astral$getSpraySource();

    int astral$getSprayTicks();

    default int astral$getSprayCharges() {
        return 1;
    }

    default boolean astral$isSprayed() {
        return this.astral$getSprayTicks() > 0;
    }

    default boolean astral$canSpray() {
        return !this.astral$isSprayed();
    }

    default void astral$sprayTick() {
        if (this.astral$isSprayed()) {
            final int ticks = this.astral$getSprayTicks() - 1;

            this.astral$setSprayed(this.astral$getSpraySource(), ticks, false);
        } else {
            this.astral$setUnsprayed();
        }
    }

    class EscapeSprayGoal<T extends PathAwareEntity & SprayableEntity> extends Goal {

        protected final T entity;
        protected final double speed;

        protected @Nullable Path path;

        public EscapeSprayGoal(T entity, double speed) {
            this.entity = entity;
            this.speed = speed;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected boolean findTarget() {
            final LivingEntity source = this.entity.astral$getSpraySource();

            if (source == null) return false;

            final Vec3d target = NoPenaltyTargeting.findFrom(this.entity, 16, 7, source.getPos());

            if (target == null) return false;

            if ((source).squaredDistanceTo(target.x, target.y, target.z) < source.squaredDistanceTo(this.entity)) {
                return false;
            }

            this.path = this.entity.getNavigation().findPathTo(target.getX(), target.getY(), target.getZ(), 0);

            return this.path != null;
        }

        @Override
        public boolean canStart() {
            return this.entity.astral$isSprayed() && this.findTarget();
        }

        @Override
        public void start() {
            this.entity.getNavigation().startMovingAlong(this.path, this.speed);
        }

        @Override
        public boolean shouldContinue() {
            return this.entity.astral$isSprayed() && !this.entity.getNavigation().isIdle();
        }

        @Override
        public void stop() {
            this.path = null;
        }

    }

}
