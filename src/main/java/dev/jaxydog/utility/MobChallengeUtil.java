package dev.jaxydog.utility;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import dev.jaxydog.content.CustomGamerules;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Provides utility methods for dealing with mod challenge scaling. */
@NonExtendable
public interface MobChallengeUtil {

    /** Returns whether mob challenge scaling is enabled. */
    public static boolean isEnabled(World world) {
        return world.getGameRules().getBoolean(CustomGamerules.CHALLENGE_ENABLED);
    }

    /** Returns the world's configured chunk step size. */
    public static int getChunkStep(World world) {
        return world.getGameRules().getInt(CustomGamerules.CHALLENGE_CHUNK_STEP);
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
        final BlockPos spawn = entity.getWorld().getSpawnPos();

        return Math.sqrt(entity.getBlockPos().getSquaredDistance(spawn));
    }

    /** Returns a statistic additive that has been scaled using the mob challenge configuration. */
    public static double getScaledAdditive(Entity entity, double additive) {
        final World world = entity.getWorld();
        final int step = MobChallengeUtil.getChunkStep(world);
        final double distance = MobChallengeUtil.getSpawnDistance(entity);
        final double modifier = Math.max(0.0D, additive) * ((distance / 16.0D) / step);

        return world.getRegistryKey().equals(World.OVERWORLD) ? modifier : modifier / 2.0;
    }

}
