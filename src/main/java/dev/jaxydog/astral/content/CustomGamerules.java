package dev.jaxydog.astral.content;

import dev.jaxydog.astral.utility.register.AutoRegister;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules.BooleanRule;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.GameRules.IntRule;
import net.minecraft.world.GameRules.Key;

/** Contains commonly shared gamerules */
@AutoRegister
public final class CustomGamerules {

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

	private CustomGamerules() {}
}
