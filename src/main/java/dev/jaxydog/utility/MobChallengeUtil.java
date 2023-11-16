package dev.jaxydog.utility;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import dev.jaxydog.Astral;
import dev.jaxydog.content.CustomGamerules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Provides utility methods for dealing with mod challenge scaling. */
@NonExtendable
public interface MobChallengeUtil {
	/** The block position used as the world origin. */
	public static final BlockPos ORIGIN = new BlockPos(0, 63, 0);
	/** An NBT key that tells an entity to ignore challenge scaling. */
	public static final String IGNORE_KEY = "IgnoreChallengeScaling";
	/** A tag that determines which entities are scaled. */
	public static final TagKey<EntityType<?>> SCALED_ENTITIES =
			TagKey.of(RegistryKeys.ENTITY_TYPE, Astral.getId("scaled_entities"));

	/** Determines whether a given entity should have scaling applied */
	public static boolean shouldScale(Entity entity) {
		return entity instanceof LivingEntity living
				&& MobChallengeUtil.isEnabled(living.getWorld())
				&& living.getType().isIn(MobChallengeUtil.SCALED_ENTITIES)
				&& !((LivingEntityMixinAccess) living).ignoresChallengeScaling();
	}

	/** Returns whether mob challenge scaling is enabled. */
	public static boolean isEnabled(World world) {
		return world.getGameRules().getBoolean(CustomGamerules.CHALLENGE_ENABLED);
	}

	/** Returns the world's configured chunk step size. */
	public static int getChunkStep(World world) {
		return Math.max(world.getGameRules().getInt(CustomGamerules.CHALLENGE_CHUNK_STEP), 1);
	}

	/** Returns the world's configured attack additive value. */
	public static double getAttackAdditive(World world) {
		return world.getGameRules().get(CustomGamerules.CHALLENGE_ATTACK_ADDITIVE).get();
	}

	/** Returns the world's configured health additive value. */
	public static double getHealthAdditive(World world) {
		return world.getGameRules().get(CustomGamerules.CHALLENGE_HEALTH_ADDITIVE).get();
	}

	/** Returns the given entity's distance to the world spawn. */
	public static double getSpawnDistance(Entity entity) {
		final boolean useOrigin = !entity.getWorld().getGameRules()
				.get(CustomGamerules.CHALLENGE_USE_WORLDSPAWN).get();
		final BlockPos center =
				useOrigin ? MobChallengeUtil.ORIGIN : entity.getWorld().getSpawnPos();

		return Math.sqrt(entity.getBlockPos().getSquaredDistance(center));
	}

	/** Returns a statistic additive that has been scaled using the mob challenge configuration. */
	public static double getScaledAdditive(Entity entity, double additive) {
		final int step = MobChallengeUtil.getChunkStep(entity.getWorld());
		final double distance = MobChallengeUtil.getSpawnDistance(entity);
		final double modifier = Math.max(0.0D, additive) * ((distance / 16.0D) / step);
		final boolean overworld = entity.getWorld().getRegistryKey().equals(World.OVERWORLD);

		return overworld ? modifier : modifier / 2.0;
	}

}
