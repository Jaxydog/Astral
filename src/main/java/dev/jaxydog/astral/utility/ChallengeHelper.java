package dev.jaxydog.astral.utility;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules.BooleanRule;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.GameRules.IntRule;
import net.minecraft.world.GameRules.Key;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Provides utility methods for dealing with mob challenge scaling */
@NonExtendable
public interface ChallengeHelper {
	/** Defines the gamerule that toggles mob challenge on or off */
	public static final Key<BooleanRule> CHALLENGE_ENABLED = GameRuleRegistry.register(
		"challengeEnabled",
		Category.MOBS,
		GameRuleFactory.createBooleanRule(true)
	);
	/** Defines the gamerule that configures the challenge chunk step size */
	public static final Key<IntRule> CHALLENGE_CHUNK_STEP = GameRuleRegistry.register(
		"challengeChunkStep",
		Category.MOBS,
		GameRuleFactory.createIntRule(16)
	);
	/** Defines the gamerule that configures how many attack points are added every chunk step */
	public static final Key<IntRule> CHALLENGE_ATTACK_ADDITIVE = GameRuleRegistry.register(
		"challengeAttackAdditive",
		Category.MOBS,
		GameRuleFactory.createIntRule(1)
	);
	/** Defines the gamerule that configures how many health points are added every chunk step */
	public static final Key<IntRule> CHALLENGE_HEALTH_ADDITIVE = GameRuleRegistry.register(
		"challengeHealthAdditive",
		Category.MOBS,
		GameRuleFactory.createIntRule(1)
	);

	/** Returns whether mob challenge scaling is enabled */
	public static boolean isEnabled(World world) {
		return world.getGameRules().getBoolean(ChallengeHelper.CHALLENGE_ENABLED);
	}

	/** Returns the world's configured chunk step size */
	public static int getChunkStep(World world) {
		return world.getGameRules().getInt(ChallengeHelper.CHALLENGE_CHUNK_STEP);
	}

	/** Returns the world's configured attack additive value */
	public static int getAttackAdditive(World world) {
		return world.getGameRules().getInt(ChallengeHelper.CHALLENGE_ATTACK_ADDITIVE);
	}

	/** Returns the world's configured health additive value */
	public static int getHealthAdditive(World world) {
		return world.getGameRules().getInt(ChallengeHelper.CHALLENGE_HEALTH_ADDITIVE);
	}

	/** Returns the given entity's distance to the world spawn */
	public static double getDistanceFromSpawn(Entity entity) {
		var spawn = entity.getWorld().getSpawnPos();
		var center = new Vec3d(spawn.getX(), entity.getY(), spawn.getZ());

		return entity.getPos().distanceTo(center);
	}

	/** Returns a stat modifier calculated using the provided values */
	public static double getChallengeModifier(int step, int additive, double distance) {
		return Math.max(0, additive) * ((distance / 16) / step);
	}

	/** Returns a stat modifier calculated using the provided values */
	public static double getChallengeModifier(Entity entity, int step, int additive) {
		var distance = ChallengeHelper.getDistanceFromSpawn(entity);

		return ChallengeHelper.getChallengeModifier(step, additive, distance);
	}

	/** Returns a stat modifier calculated using the provided values */
	public static double getChallengeModifier(Entity entity, int additive) {
		var distance = ChallengeHelper.getDistanceFromSpawn(entity);
		var step = ChallengeHelper.getChunkStep(entity.getWorld());

		return ChallengeHelper.getChallengeModifier(step, additive, distance);
	}
}
