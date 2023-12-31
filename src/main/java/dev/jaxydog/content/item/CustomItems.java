package dev.jaxydog.content.item;

import dev.jaxydog.content.block.CustomBlocks;
import dev.jaxydog.content.item.custom.CloudyItem;
import dev.jaxydog.content.item.custom.DyeableCloudyArmorItem;
import dev.jaxydog.content.item.custom.MirrorItem;
import dev.jaxydog.content.item.custom.PlaceholderItem;
import dev.jaxydog.register.ContentRegistrar;
import dev.jaxydog.utility.ArmorMap;
import dev.jaxydog.utility.DyeableMap;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;

/** Contains definitions for all custom items */
public final class CustomItems extends ContentRegistrar {

	public static final CloudyItem CLOUDY_CANDY = new CloudyItem("cloudy_candy",
		new Settings().food(FoodComponents.CLOUDY_CANDY_FOOD).rarity(Rarity.UNCOMMON)
	);
	public static final CloudyItem CLOUDY_MANE = new CloudyItem("cloudy_mane", new Settings().rarity(Rarity.UNCOMMON));
	public static final CloudyItem CLOUDY_COTTON = new CloudyItem("cloudy_cotton",
		new Settings().rarity(Rarity.UNCOMMON)
	);
	public static final ArmorMap<DyeableCloudyArmorItem> CLOUD_ARMOR = new ArmorMap<>("cloudy",
		(rawId, type) -> new DyeableCloudyArmorItem(rawId, ArmorMaterials.CLOUDY, type, new Settings())
	);

	public static final MirrorItem MIRROR = new MirrorItem("mirror", new Settings().maxCount(1).rarity(Rarity.RARE));
	public static final PlaceholderItem PLACEHOLDER = new PlaceholderItem("placeholder",
		new Settings().fireproof().maxCount(1).rarity(Rarity.UNCOMMON)
	);

	public static final DyeableMap<CustomBlockItem> DYED_AMETHYST_BLOCKS = new DyeableMap<>("amethyst_block",
		(rawId, color) -> new CustomBlockItem(rawId, CustomBlocks.DYED_AMETHYST_BLOCKS.get(color), new Settings())
	);
	public static final DyeableMap<CustomBlockItem> DYED_AMETHYST_CLUSTERS = new DyeableMap<>("amethyst_cluster",
		(rawId, color) -> new CustomBlockItem(rawId,
			CustomBlocks.DYED_AMETHYST_CLUSTER_BLOCKS.get(color),
			new Settings()
		)
	);

	public static final CustomBlockItem RANDOMIZER_BLOCK = new CustomBlockItem("randomizer",
		CustomBlocks.RANDOMIZER,
		new Settings().rarity(Rarity.UNCOMMON)
	);

	private static final class FoodComponents {

		public static final FoodComponent CLOUDY_CANDY_FOOD = new FoodComponent.Builder().alwaysEdible()
			.hunger(2)
			.saturationModifier(0.45F)
			.snack()
			.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0, false, true, true), 0.25F)
			.build();

		private FoodComponents() {}

	}

	private static final class ArmorMaterials {

		public static final CustomArmorMaterial CLOUDY = CustomArmorMaterial.builder("cloud")
			.setDurability(52, 64, 60, 44)
			.setEnchantability(15)
			.setEquipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
			.setProtectionAmount(2, 5, 3, 2)
			.setRepairIngredient(Ingredient.ofItems(CLOUDY_COTTON))
			.build();

		private ArmorMaterials() {}

	}

}
