package dev.jaxydog.astral.content.item;

import dev.jaxydog.astral.content.block.CustomBlocks;
import dev.jaxydog.astral.content.block.custom.DyeableAmethystBlock;
import dev.jaxydog.astral.content.block.custom.DyeableAmethystClusterBlock;
import dev.jaxydog.astral.content.block.custom.DyeableBuddingAmethystBlock;
import dev.jaxydog.astral.content.effect.CustomStatusEffects;
import dev.jaxydog.astral.content.item.AstralArmorItem.Material;
import dev.jaxydog.astral.content.item.custom.*;
import dev.jaxydog.astral.datagen.TagGenerator;
import dev.jaxydog.astral.register.ArmorMap;
import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.DyeableMap;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;

/** Contains definitions for all custom items */
@SuppressWarnings("unused")
public final class AstralItems extends ContentRegistrar {

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

    public static final DyeableMap<AstralBlockItem> DYEABLE_AMETHYST_BLOCKS = new DyeableMap<>("amethyst_block",
        (rawId, color) -> new AstralBlockItem(rawId,
            CustomBlocks.DYEABLE_AMETHYST_BLOCKS.get(color),
            new Settings(),
            CustomItemGroups.DYEABLE_AMETHYST::getRegistryKey
        )
    );
    public static final DyeableMap<AstralBlockItem> DYEABLE_BUDDING_AMETHYST_BLOCKS = new DyeableMap<>("budding_amethyst",
        (rawId, color) -> new AstralBlockItem(rawId,
            CustomBlocks.DYEABLE_BUDDING_AMETHYST_BLOCKS.get(color),
            new Settings(),
            CustomItemGroups.DYEABLE_AMETHYST::getRegistryKey
        )
    );
    public static final DyeableMap<AstralBlockItem> DYEABLE_AMETHYST_CLUSTERS = new DyeableMap<>("amethyst_cluster",
        (rawId, color) -> new AstralBlockItem(rawId,
            CustomBlocks.DYEABLE_AMETHYST_CLUSTERS.get(color),
            new Settings(),
            CustomItemGroups.DYEABLE_AMETHYST::getRegistryKey
        )
    );
    public static final DyeableMap<AstralBlockItem> DYEABLE_LARGE_AMETHYST_BUDS = new DyeableMap<>("large_amethyst_bud",
        (rawId, color) -> new AstralBlockItem(rawId,
            CustomBlocks.DYEABLE_LARGE_AMETHYST_BUDS.get(color),
            new Settings(),
            CustomItemGroups.DYEABLE_AMETHYST::getRegistryKey
        )
    );
    public static final DyeableMap<AstralBlockItem> DYEABLE_MEDIUM_AMETHYST_BUDS = new DyeableMap<>("medium_amethyst_bud",
        (rawId, color) -> new AstralBlockItem(rawId,
            CustomBlocks.DYEABLE_MEDIUM_AMETHYST_BUDS.get(color),
            new Settings(),
            CustomItemGroups.DYEABLE_AMETHYST::getRegistryKey
        )
    );
    public static final DyeableMap<AstralBlockItem> DYEABLE_SMALL_AMETHYST_BUDS = new DyeableMap<>("small_amethyst_bud",
        (rawId, color) -> new AstralBlockItem(rawId,
            CustomBlocks.DYEABLE_SMALL_AMETHYST_BUDS.get(color),
            new Settings(),
            CustomItemGroups.DYEABLE_AMETHYST::getRegistryKey
        )
    );

    public static final SprayBottleItem SPRAY_BOTTLE = new SprayBottleItem("spray_bottle",
        new Settings().maxDamage(SprayBottleItem.MAX_USES)
    );
    public static final SprayPotionItem SPRAY_POTION = new SprayPotionItem("spray_potion",
        new Settings().maxDamage(SprayPotionItem.MAX_USES)
    );
    public static final AstralBlockItem RANDOMIZER_BLOCK = new AstralBlockItem("randomizer",
        CustomBlocks.RANDOMIZER,
        new Settings().rarity(Rarity.UNCOMMON)
    );

    // Milk Items by Ice(The Woman)
    public static final ChocolateMilkItem CHOCOLATE_MILK = new ChocolateMilkItem("chocolate_milk",
        new Settings().food(FoodComponents.CHOCOLATE_MILK).maxCount(16)
    );
    public static final BottleItem STRAWBERRY_MILK = new BottleItem("strawberry_milk",
        new Settings().food(FoodComponents.STRAWBERRY_MILK).maxCount(16)
    );

    // Items for star monkey
    public static final AstralItem ROTTEN_CHORUS_FRUIT = new AstralItem("rotten_chorus_fruit",
        new Settings().food(FoodComponents.ROTTEN_CHORUS_FRUIT),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );
    public static final AstralItem LIVING_SCULK = new AstralItem("living_sculk",
        new Settings().rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );
    public static final AstralItem PIG_CARD = new AstralItem("pig_card",
        new Settings(),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );
    public static final AstralItem SLIME_CARD = new AstralItem("slime_card",
        new Settings(),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );
    public static final AstralItem APPY_SAUCE = new AstralItem("appy_sauce",
        new Settings(),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );
    public static final AstralItem VOID_ESSENCE = new AstralItem("void_essence",
        new Settings().rarity(Rarity.EPIC),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );
    public static final AstralItem DRAGON_SCALE = new AstralItem("dragon_scale",
        new Settings().rarity(Rarity.EPIC),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey
    );

    public static final RandomEffectItem CLOCK_OF_REGRET = new RandomEffectItem("clock_of_regret",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.NAUSEA
    );
    public static final RandomEffectItem CUP_OF_GRIEF = new RandomEffectItem("cup_of_grief",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.SLOWNESS
    );
    public static final RandomEffectItem SKULL_OF_JOY = new RandomEffectItem("skull_of_joy",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.REGENERATION
    );
    public static final RandomEffectItem BLOB_OF_MALINTENT = new RandomEffectItem("blob_of_malintent",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        CustomStatusEffects.SINISTER
    );
    public static final RandomEffectItem COOKIE_OF_RESENTMENT = new RandomEffectItem("cookie_of_resentment",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.STRENGTH
    );
    public static final RandomEffectItem EYE_OF_SURPRISE = new RandomEffectItem("eye_of_surprise",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.LEVITATION
    );
    public static final RandomEffectItem TARGET_OF_PANIC = new RandomEffectItem("target_of_panic",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.SPEED
    );
    public static final RandomEffectItem PHOTO_OF_HOPE = new RandomEffectItem("photo_of_hope",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.LUCK
    );
    public static final RandomEffectItem CROWN_OF_DREAD = new RandomEffectItem("crown_of_dread",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.DARKNESS
    );
    public static final RandomEffectItem EGG_OF_GREED = new RandomEffectItem("egg_of_greed",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.HERO_OF_THE_VILLAGE
    );
    public static final RandomEffectItem FLOWER_OF_SUFFERING = new RandomEffectItem("flower_of_suffering",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.WITHER
    );
    public static final RandomEffectItem BULB_OF_REJECTION = new RandomEffectItem("bulb_of_rejection",
        new Settings().maxCount(1).rarity(Rarity.RARE),
        CustomItemGroups.STARMONEY_PLAZA::getRegistryKey,
        0.0005F,
        StatusEffects.INVISIBILITY
    );

    @Override
    public void generate() {
        super.generate();

        TagGenerator.getInstance()
            .generate(DyeableAmethystBlock.AMETHYST_BLOCK_ITEMS, b -> b.add(Items.AMETHYST_BLOCK));
        TagGenerator.getInstance()
            .generate(DyeableBuddingAmethystBlock.BUDDING_AMETHYST_ITEMS, b -> b.add(Items.BUDDING_AMETHYST));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.AMETHYST_CLUSTER_ITEMS, b -> b.add(Items.AMETHYST_CLUSTER));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.LARGE_AMETHYST_BUD_ITEMS, b -> b.add(Items.LARGE_AMETHYST_BUD));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.MEDIUM_AMETHYST_BUD_ITEMS, b -> b.add(Items.MEDIUM_AMETHYST_BUD));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.SMALL_AMETHYST_BUD_ITEMS, b -> b.add(Items.SMALL_AMETHYST_BUD));
    }

    private static final class FoodComponents {

        public static final FoodComponent CHOCOLATE_MILK = new FoodComponent.Builder().alwaysEdible()
            .hunger(6)
            .saturationModifier(0.25F)
            .build();

        public static final FoodComponent CLOUDY_CANDY_FOOD = new FoodComponent.Builder().alwaysEdible()
            .hunger(2)
            .saturationModifier(0.45F)
            .snack()
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0, false, true, true), 0.25F)
            .build();

        public static final FoodComponent ROTTEN_CHORUS_FRUIT = new FoodComponent.Builder().alwaysEdible()
            .hunger(1)
            .saturationModifier(0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 0), 1F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 400, 0), 1F)
            .build();

        public static final FoodComponent STRAWBERRY_MILK = new FoodComponent.Builder().alwaysEdible()
            .hunger(7)
            .saturationModifier(0.25F)
            .build();

        private FoodComponents() {}

    }

    private static final class ArmorMaterials {

        public static final Material CLOUDY = Material.builder("cloud")
            .setDurability(52, 64, 60, 44)
            .setEnchantability(15)
            .setEquipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
            .setProtection(2, 5, 3, 2)
            .setRepairIngredient(Ingredient.ofItems(CLOUDY_COTTON))
            .build();

        private ArmorMaterials() {}

    }

}
