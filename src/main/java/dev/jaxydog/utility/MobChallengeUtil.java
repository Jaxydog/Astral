package dev.jaxydog.utility;

import dev.jaxydog.Astral;
import dev.jaxydog.content.CustomGamerules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/** Provides utility methods for dealing with mod challenge scaling. */
@NonExtendable
public interface MobChallengeUtil {

	/** The block position used as the world origin. */
	BlockPos ORIGIN = new BlockPos(0, 63, 0);
	/** An NBT key that tells an entity to ignore challenge scaling. */
	String IGNORE_KEY = "IgnoreChallengeScaling";
	/** An NBT key that tells an entity to force challenge scaling. */
	String FORCE_KEY = "ForceChallengeScaling";
	/** A tag that determines which entities are scaled. */
	TagKey<EntityType<?>> SCALED_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, Astral.getId("challenge"));

	/** Determines whether a given entity should have scaling applied */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static boolean shouldScale(Entity entity) {
		return entity instanceof final LivingEntity living
			&& (((LivingEntityMixinAccess) living).astral$forcesChallengeScaling() || (isEnabled(living.getWorld())
			&& living.getType().isIn(SCALED_ENTITIES)
			&& !((LivingEntityMixinAccess) living).astral$ignoresChallengeScaling()
			&& (!(living instanceof final TameableEntity tamable) || !tamable.isTamed())));
	}

	/** Returns whether mob challenge scaling is enabled. */
	static boolean isEnabled(World world) {
		return world.getGameRules().getBoolean(CustomGamerules.CHALLENGE_ENABLED);
	}

	/** Returns the world's configured attack additive value. */
	static double getAttackAdditive(World world) {
		return world.getGameRules().get(CustomGamerules.CHALLENGE_ATTACK_ADDITIVE).get();
	}

	/** Returns the world's configured health additive value. */
	static double getHealthAdditive(World world) {
		return world.getGameRules().get(CustomGamerules.CHALLENGE_HEALTH_ADDITIVE).get();
	}

	/** Returns a statistic additive that has been scaled using the mob challenge configuration. */
	static double getScaledAdditive(Entity entity, double additive) {
		if (entity == null || entity.getWorld() == null) return additive;

		final int step = getChunkStep(entity.getWorld());
		final double distance = getSpawnDistance(entity);
		final double modifier = Math.max(0D, additive) * ((distance / 16D) / step);
		final boolean overworld = entity.getWorld().getRegistryKey().equals(World.OVERWORLD);

		return overworld ? modifier : modifier / 2D;
	}

	/** Returns the world's configured chunk step size. */
	static int getChunkStep(World world) {
		return Math.max(world.getGameRules().getInt(CustomGamerules.CHALLENGE_CHUNK_STEP), 1);
	}

	/** Returns the given entity's distance to the world spawn. */
	static double getSpawnDistance(Entity entity) {
		final boolean useOrigin = !entity.getWorld().getGameRules().get(CustomGamerules.CHALLENGE_USE_WORLDSPAWN).get();
		final BlockPos center = useOrigin ? ORIGIN : entity.getWorld().getSpawnPos();

		return Math.sqrt(entity.getBlockPos().getSquaredDistance(center));
	}

}
