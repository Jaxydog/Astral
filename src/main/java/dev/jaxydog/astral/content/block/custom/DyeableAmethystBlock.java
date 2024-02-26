/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.content.block.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.content.block.CustomBlock;
import dev.jaxydog.content.item.CustomBlockItem;
import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.datagen.*;
import dev.jaxydog.register.Generated;
import dev.jaxydog.utility.ColorUtil.Rgb;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.Optional;

/**
 * Implements dyed amethyst blocks.
 *
 * @author Jaxydog
 */
public class DyeableAmethystBlock extends CustomBlock implements Generated {

    /** A block tag containing all amethyst blocks. */
    public static final TagKey<Block> AMETHYST_BLOCKS = TagKey.of(Registries.BLOCK.getKey(),
        Astral.getId("amethyst_blocks")
    );
    /** An item tag containing all amethyst blocks. */
    public static final TagKey<Item> AMETHYST_BLOCK_ITEMS = TagKey.of(Registries.ITEM.getKey(),
        Astral.getId("amethyst_blocks")
    );

    /** This block's dye color. */
    private final DyeColor color;

    public DyeableAmethystBlock(String rawId, Settings settings, DyeColor color) {
        super(rawId, settings);

        this.color = color;
    }

    protected static int convertColor(Rgb rgb, DyeColor color) {
        final float s = rgb.saturation();
        final float b = rgb.brightness();

        // Some colors have special rules.
        return (switch (color) {
            case WHITE -> rgb.withSaturation(0F);
            case LIGHT_GRAY -> rgb.withSaturation(0F).withBrightness(b - 0.225F);
            case GRAY -> rgb.withSaturation(0F).withBrightness(b - 0.45F);
            case BLACK -> rgb.withSaturation(0F).withBrightness(b - 0.6F);
            default -> {
                // All other colors just hue rotate.
                final float[] components = color.getColorComponents();
                final Rgb target = new Rgb(components[0], components[1], components[2]);
                final Rgb rotated = rgb.withHue(target.hue());

                yield switch (color) {
                    // These specific colors hue rotate *and* have differing brightness / saturation values.
                    case BROWN -> rotated.withBrightness(b - 0.25F);
                    case GREEN -> rotated.withSaturation(s + 0.375F).withBrightness(b - 0.325F);
                    case PINK -> rotated.withSaturation(s - 0.1F).withBrightness(b + 0.1F);
                    case CYAN -> rotated.withSaturation(s + 0.25F).withBrightness(b - 0.25F);
                    // Everything else just increases in saturation.
                    default -> rotated.withSaturation(s + 0.25F);
                };
            }
        }).asInt() | 0xFF000000; // Re-introduce full alpha color.
    }

    protected static void contrastFilter(BufferedImage image, DyeColor color) {
        final float percentage = switch (color) {
            case GRAY -> 0.625F;
            case BLACK -> 0.5F;
            case GREEN -> 0.875F;
            default -> 1F;
        };

        if (percentage != 1F) new RescaleOp(percentage, 0xF, null).filter(image, image);
    }

    protected static void generateTexture(
        TextureGenerator.Instance<Block> instance, String baseTexture, DyeColor color, Identifier identifier
    ) {
        final Optional<BufferedImage> maybeSource = instance.getImage(baseTexture);

        if (maybeSource.isEmpty()) return;

        final BufferedImage source = maybeSource.get();
        final BufferedImage generated = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());

        // Compute all pixels.
        for (int y = 0; y < source.getHeight(); y += 1) {
            for (int x = 0; x < source.getWidth(); x += 1) {
                final int argb = source.getRGB(x, y);

                // Ignore if the pixel is transparent.
                if ((argb & 0xFF000000) == 0) continue;

                generated.setRGB(x, y, convertColor(new Rgb(argb), color));
            }
        }

        contrastFilter(generated, color);

        instance.generate(identifier, generated);
    }

    /**
     * Returns this block's associated item.
     *
     * @return The associated item.
     */
    public CustomBlockItem getItem() {
        return CustomItems.DYEABLE_AMETHYST_BLOCKS.get(this.getColor());
    }

    /**
     * Returns this block's dye color.
     *
     * @return The associated color.
     */
    public DyeColor getColor() {
        return this.color;
    }

    protected void playChimeSound(World world, BlockPos pos, SoundEvent event) {
        final float pitch = 0.5F + world.getRandom().nextFloat() * 1.2F;

        world.playSound(null, pos, event, SoundCategory.BLOCKS, 1F, pitch);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (world.isClient()) return;

        final BlockPos position = hit.getBlockPos();

        this.playChimeSound(world, position, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
        this.playChimeSound(world, position, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
    }

    @Override
    public void generate() {
        ModelGenerator.getInstance().generateBlock(g -> g.registerSimpleCubeAll(this));
        TagGenerator.getInstance().generate(AMETHYST_BLOCKS, b -> b.add(this));
        TagGenerator.getInstance().generate(AMETHYST_BLOCK_ITEMS, b -> b.add(this.getItem()));
        TagGenerator.getInstance().generate(BlockTags.PICKAXE_MINEABLE, b -> b.add(this));
        TextureGenerator.getInstance().generate(Registries.BLOCK.getKey(),
            i -> generateTexture(i, "amethyst_block", this.getColor(), this.getRegistryId())
        );
        LootTableGenerator.getInstance().generate(LootContextTypes.BLOCK,
            this.lootTableId,
            new Builder().pool(LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(this::getItem))
                .conditionally(SurvivesExplosionLootCondition.builder().build())
                .build())
        );
        RecipeGenerator.getInstance().generate(this.getItem().getRegistryId(),
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, this.getItem())
                .group("dyeable_amethyst_blocks")
                .input(AMETHYST_BLOCK_ITEMS)
                .input(DyeItem.byColor(this.getColor()))
                .criterion("block", FabricRecipeProvider.conditionsFromTag(AMETHYST_BLOCK_ITEMS))
        );
        LanguageGenerator.getInstance().generate(builder -> {
            final String[] parts = this.getColor().getName().split("_");
            final String value = Arrays.stream(parts)
                .map(s -> StringUtils.capitalize(s) + " ")
                .reduce(String::concat)
                .orElse("Dyed ");

            builder.add(this, value + "Amethyst Block");
        });
    }

}
