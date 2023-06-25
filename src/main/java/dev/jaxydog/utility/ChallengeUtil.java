package dev.jaxydog.utility;

import dev.jaxydog.content.CustomGamerules;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Provides utility mehtods for dealing with mob challenge scaling */
@NonExtendable
public interface ChallengeUtil {

	/** Returns whether mob challenge scaling is enabled */
	public static boolean isEnabled(World world) {
		return world.getGameRules().getBoolean(CustomGamerules.CHALLENGE_ENABLED);
	}

	/** Returns the world's configured chunk step size */
	public static int getChunkStep(World world) {
		return world.getGameRules().getInt(CustomGamerules.CHALLENGE_CHUNK_STEP);
	}

	/** Returns the world's configured attack additive value */
	public static int getAttackAdditive(World world) {
		return world.getGameRules().getInt(CustomGamerules.CHALLENGE_ATTACK_ADDITIVE);
	}

	/** Returns the world's configured health additive value */
	public static int getHealthAdditive(World world) {
		return world.getGameRules().getInt(CustomGamerules.CHALLENGE_HEALTH_ADDITIVE);
	}

	/** Returns the given entity's distance to the world spawn */
	public static double getDistanceFromSpawn(Entity entity) {
		BlockPos spawn = entity.getWorld().getSpawnPos();
		Vec3d center = new Vec3d(spawn.getX(), entity.getY(), spawn.getZ());

		return entity.getPos().distanceTo(center);
	}

	/** Returns a stat modifier calculated using the provided values */
	public static double getChallengeModifier(int step, int additive, double distance) {
		return Math.max(0, additive) * ((distance / 16) / step);
	}

	/** Returns a stat modifier calculated using the provided values */
	public static double getChallengeModifier(Entity entity, int step, int additive) {
		double distance = ChallengeUtil.getDistanceFromSpawn(entity);

		return ChallengeUtil.getChallengeModifier(step, additive, distance);
	}

	/** Returns a stat modifier calculated using the provided values */
	public static double getChallengeModifier(Entity entity, int additive) {
		double distance = ChallengeUtil.getDistanceFromSpawn(entity);
		int step = ChallengeUtil.getChunkStep(entity.getWorld());

		return ChallengeUtil.getChallengeModifier(step, additive, distance);
	}

}
