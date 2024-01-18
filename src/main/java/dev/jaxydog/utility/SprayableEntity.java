package dev.jaxydog.utility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface SprayableEntity {

	void astral$setSprayDuration(LivingEntity source, int ticks);

	@Nullable LivingEntity astral$getSpraySource();

	int astral$getSprayDuration();

	class EscapeSprayGoal<T extends PathAwareEntity & SprayableEntity> extends Goal {

		public static final int RANGE_Y = 1;

		protected final T entity;
		protected final double speed;

		protected boolean active = false;
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
			if (this.entity.astral$getSprayDuration() <= 0) {
				return false;
			} else {
				return this.findTarget();
			}
		}

		@Override
		public void start() {
			this.entity.getNavigation().startMovingAlong(this.path, this.speed);
		}

		@Override
		public boolean shouldContinue() {
			return this.entity.astral$getSprayDuration() > 0 && !this.entity.getNavigation().isIdle();
		}

		@Override
		public void stop() {
			this.path = null;
		}

	}

}
