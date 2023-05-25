package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.content.block.CustomBlocks;
import dev.jaxydog.astral.content.item.CustomArmorItem.SetHelper;
import dev.jaxydog.astral.content.item.custom.CloudArmorItem;
import dev.jaxydog.astral.content.item.custom.CloudItem;
import dev.jaxydog.astral.content.item.custom.CurrencyItem;
import dev.jaxydog.astral.content.item.custom.CurrencyRewardItem;
import dev.jaxydog.astral.content.item.custom.CurrencySkeletonItem;
import dev.jaxydog.astral.utility.DyeableSet;
import dev.jaxydog.astral.utility.register.AutoRegister;
import java.util.HashMap;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;

/** Contains definitions for all custom items */
@AutoRegister
public class CustomItems {

	public static final CloudItem CLOUDY_CANDY = new CloudItem(
		"cloudy_candy",
		new Settings()
			.food(
				new FoodComponent.Builder()
					.alwaysEdible()
					.hunger(2)
					.saturationModifier(0.45f)
					.snack()
					.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0, false, true, true), 0.25F)
					.build()
			)
			.rarity(Rarity.UNCOMMON)
	);
	public static final CloudItem CLOUDY_COTTON = new CloudItem(
		"cloudy_cotton",
		new Settings().rarity(Rarity.UNCOMMON)
	);
	public static final CloudItem CLOUDY_MANE = new CloudItem("cloudy_mane", new Settings().rarity(Rarity.UNCOMMON));
	public static final SetHelper<CloudArmorItem> CLOUD_ARMOR = new SetHelper<>(
		"cloud",
		(id, type) ->
			new CloudArmorItem(
				id,
				new CustomArmorMaterial("cloud")
					.setDurability(4.0F)
					.setEnchantability(15)
					.setEquipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
					.setProtectionAmount(2, 5, 3, 2)
					.setRepairIngredient(Ingredient.ofItems(CustomItems.CLOUDY_COTTON)),
				type,
				new Settings()
			)
	);

	public static final CurrencyItem CURRENCY = new CurrencyItem("currency", new Settings().rarity(Rarity.UNCOMMON));
	public static final CurrencyRewardItem CURRENCY_REWARD = new CurrencyRewardItem(
		"currency_reward",
		new Settings().maxCount(16).rarity(Rarity.RARE),
		new HashMap<>(0)
	);
	public static final CurrencySkeletonItem CURRENCY_SKELETON = new CurrencySkeletonItem(
		"currency_skeleton",
		new Settings().maxCount(1).rarity(Rarity.EPIC)
	);

	public static final DyeableSet<CustomBlockItem> DYED_AMETHYST_BLOCK = new DyeableSet<>(
		"amethyst_block",
		(id, color) -> new CustomBlockItem(id, CustomBlocks.DYED_AMETHYST_BLOCK.get(color), new Settings())
	);
	public static final DyeableSet<CustomBlockItem> DYED_AMETHYST_CLUSTER = new DyeableSet<>(
		"amethyst_cluster",
		(id, color) -> new CustomBlockItem(id, CustomBlocks.DYED_AMETHYST_CLUSTER.get(color), new Settings())
	);

	public static final CustomBlockItem RANDOMIZER_BLOCK = new CustomBlockItem(
		"randomizer",
		CustomBlocks.RANDOMIZER,
		new Settings().rarity(Rarity.UNCOMMON)
	);

	private CustomItems() {}
}
