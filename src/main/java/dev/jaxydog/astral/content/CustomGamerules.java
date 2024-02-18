package dev.jaxydog.astral.content;

import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.IgnoreRegistration;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules.BooleanRule;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.GameRules.IntRule;
import net.minecraft.world.GameRules.Key;

/** Contains shared mod gamerules */
public final class CustomGamerules extends ContentRegistrar {

	/** Defines the gamerule that toggles mob challenge on or off */
	@IgnoreRegistration
	public static final Key<BooleanRule> CHALLENGE_ENABLED = GameRuleRegistry.register("challengeEnabled",
		Category.MOBS,
		GameRuleFactory.createBooleanRule(true)
	);
	/** Defines the gamerule that configures the challenge chunk step size */
	@IgnoreRegistration
	public static final Key<IntRule> CHALLENGE_CHUNK_STEP = GameRuleRegistry.register("challengeChunkStep",
		Category.MOBS,
		GameRuleFactory.createIntRule(16)
	);
	/** Defines the gamerule that configures how many attack points are added every chunk step */
	@IgnoreRegistration
	public static final Key<DoubleRule> CHALLENGE_ATTACK_ADDITIVE = GameRuleRegistry.register("challengeAttackAdditive",
		Category.MOBS,
		GameRuleFactory.createDoubleRule(1D)
	);
	/** Defines the gamerule that configures how many health points are added every chunk step */
	@IgnoreRegistration
	public static final Key<DoubleRule> CHALLENGE_HEALTH_ADDITIVE = GameRuleRegistry.register("challengeHealthAdditive",
		Category.MOBS,
		GameRuleFactory.createDoubleRule(1D)
	);
	/** Defines the gamerule that determines whether scaling uses world spawn or 0, 0. */
	@IgnoreRegistration
	public static final Key<BooleanRule> CHALLENGE_USE_WORLDSPAWN = GameRuleRegistry.register("challengeUseWorldspawn",
		Category.MOBS,
		GameRuleFactory.createBooleanRule(true)
	);

	/** Defines the gamerule that configures the chance of crafting a reward from currency */
	@IgnoreRegistration
	public static final Key<DoubleRule> CURRENCY_REWARD_CHANCE = GameRuleRegistry.register("currencyRewardChance",
		Category.DROPS,
		GameRuleFactory.createDoubleRule(0.1D)
	);

}
