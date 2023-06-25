package dev.jaxydog.content.item;

import dev.jaxydog.content.block.CustomBlocks;
import dev.jaxydog.content.item.custom.CloudItem;
import dev.jaxydog.content.item.custom.CurrencyItem;
import dev.jaxydog.content.item.custom.CurrencyRewardItem;
import dev.jaxydog.content.item.custom.CurrencySkeletonItem;
import dev.jaxydog.content.item.custom.DyeableCloudArmorItem;
import dev.jaxydog.content.item.custom.MirrorItem;
import dev.jaxydog.utility.ArmorSet;
import dev.jaxydog.utility.DyeableSet;
import dev.jaxydog.utility.register.ContentContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;

/** Contains definitions for all custom items */
public final class CustomItems extends ContentContainer {

	public static final CloudItem CLOUDY_CANDY = new CloudItem("cloudy_candy", new Settings()
			.food(CustomItems.FoodComponents.CLOUDY_CANDY_FOOD).rarity(Rarity.UNCOMMON));
	public static final CloudItem CLOUDY_MANE =
			new CloudItem("cloudy_mane", new Settings().rarity(Rarity.UNCOMMON));
	public static final CloudItem CLOUDY_COTTON =
			new CloudItem("cloudy_cotton", new Settings().rarity(Rarity.UNCOMMON));
	public static final ArmorSet<DyeableCloudArmorItem> CLOUD_ARMOR_SET =
			new ArmorSet<>("cloudy", (rawId, type) -> new DyeableCloudArmorItem(rawId,
					CustomItems.ArmorMaterials.CLOUDY, type, new Settings()));

	public static final CurrencyItem CURRENCY =
			new CurrencyItem("currency", new Settings().rarity(Rarity.UNCOMMON));
	public static final CurrencyRewardItem CURRENCY_REWARD = new CurrencyRewardItem(
			"currency_reward", new Settings().maxCount(16).rarity(Rarity.RARE));
	public static final CurrencySkeletonItem CURRENCY_SKELETON = new CurrencySkeletonItem(
			"currency_skeleton", new Settings().maxCount(1).rarity(Rarity.EPIC));

	public static final MirrorItem MIRROR =
			new MirrorItem("mirror", new Settings().maxCount(1).rarity(Rarity.RARE));

	public static final DyeableSet<CustomBlockItem> DYED_AMETHYST_BLOCK_SET =
			new DyeableSet<>("amethyst_block", (rawId, color) -> new CustomBlockItem(rawId,
					CustomBlocks.DYED_AMETHYST_BLOCK_SET.get(color), new Settings()));
	public static final DyeableSet<CustomBlockItem> DYED_AMETHYST_CLUSTER_SET =
			new DyeableSet<>("amethyst_cluster", (rawId, color) -> new CustomBlockItem(rawId,
					CustomBlocks.DYED_AMETHYST_CLUSTER_BLOCK_SET.get(color), new Settings()));

	public static final CustomBlockItem RANDOMIZER_BLOCK = new CustomBlockItem("randomizer",
			CustomBlocks.RANDOMIZER, new Settings().rarity(Rarity.UNCOMMON));

	private static final class FoodComponents {

		public static final FoodComponent CLOUDY_CANDY_FOOD = new FoodComponent.Builder()
				.alwaysEdible().hunger(2).saturationModifier(0.45F).snack()
				.statusEffect(
						new StatusEffectInstance(StatusEffects.SPEED, 100, 0, false, true, true),
						0.25F)
				.build();

		private FoodComponents() {}

	}

	private static final class ArmorMaterials {

		public static final CustomArmorMaterial CLOUDY = CustomArmorMaterial.builder("cloud")
				.setDurability(52, 64, 60, 44).setEnchantability(15)
				.setEquipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER).setProtectionAmount(2, 5, 3, 2)
				.setRepairIngredient(Ingredient.ofItems(CustomItems.CLOUDY_COTTON)).build();

		private ArmorMaterials() {}

	}

}
