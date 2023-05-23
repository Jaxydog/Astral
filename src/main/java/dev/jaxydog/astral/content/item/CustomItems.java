package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.content.block.CustomBlocks;
import dev.jaxydog.astral.content.item.CustomArmorItem.SetHelper;
import dev.jaxydog.astral.content.item.custom.CloudyArmorItem;
import dev.jaxydog.astral.content.item.custom.CloudyItem;
import dev.jaxydog.astral.content.item.custom.CurrencyItem;
import dev.jaxydog.astral.content.item.custom.CurrencyRewardItem;
import dev.jaxydog.astral.content.item.custom.CurrencySkeletonItem;
import dev.jaxydog.astral.utility.AutoRegister;
import dev.jaxydog.astral.utility.DyeableHelper;
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

	public static final CloudyItem CLOUDY_CANDY = new CloudyItem(
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
	public static final CloudyItem CLOUDY_COTTON = new CloudyItem(
		"cloudy_cotton",
		new Settings().rarity(Rarity.UNCOMMON)
	);
	public static final CloudyItem CLOUDY_MANE = new CloudyItem("cloudy_mane", new Settings().rarity(Rarity.UNCOMMON));
	public static final SetHelper<CloudyArmorItem> CLOUDY_ARMOR = new SetHelper<>(
		new CustomArmorMaterial("cloudy")
			.setDurability(4.0F)
			.setEnchantability(15)
			.setEquipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
			.setProtectionAmount(2, 5, 3, 2)
			.setRepairIngredient(Ingredient.ofItems(CustomItems.CLOUDY_COTTON)),
		new Settings().rarity(Rarity.UNCOMMON),
		CloudyArmorItem::new
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

	public static final DyeableHelper<CustomBlockItem> DYED_AMETHYST_BLOCK = DyeableHelper.fromDyeable(
		"amethyst_block",
		CustomBlocks.DYED_AMETHYST_BLOCK,
		new Settings(),
		CustomBlockItem::new
	);
	public static final DyeableHelper<CustomBlockItem> DYED_AMETHYST_CLUSTER = DyeableHelper.fromDyeable(
		"amethyst_cluster",
		CustomBlocks.DYED_AMETHYST_CLUSTER,
		new Settings(),
		CustomBlockItem::new
	);
}
