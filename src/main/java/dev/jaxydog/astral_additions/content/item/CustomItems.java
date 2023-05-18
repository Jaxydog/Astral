package dev.jaxydog.astral_additions.content.item;

import dev.jaxydog.astral_additions.content.item.custom.CurrencyItem;
import dev.jaxydog.astral_additions.content.item.custom.CurrencyRewardItem;
import dev.jaxydog.astral_additions.content.item.custom.CurrencySkeletonItem;
import dev.jaxydog.astral_additions.utility.AutoRegister;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Rarity;

/** Contains definitions for all custom items */
@AutoRegister
public class CustomItems {

	public static final CurrencyItem CURRENCY = new CurrencyItem("currency", new Settings().rarity(Rarity.UNCOMMON));
	public static final CurrencyRewardItem CURRENCY_REWARD = new CurrencyRewardItem(
		"currency_reward",
		new Settings().maxCount(16).rarity(Rarity.RARE)
	);
	public static final CurrencySkeletonItem CURRENCY_SKELETON = new CurrencySkeletonItem(
		"currency_skeleton",
		new Settings().maxCount(1).rarity(Rarity.EPIC)
	);
}
