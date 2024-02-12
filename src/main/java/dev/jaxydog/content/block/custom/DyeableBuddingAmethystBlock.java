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
import dev.jaxydog.content.block.CustomBlocks;
import dev.jaxydog.content.item.CustomBlockItem;
import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.fluid.Fluids;
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
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Implements dyeable budding amethyst blocks.
 *
 * @author Jaxydog
 */
public class DyeableBuddingAmethystBlock extends DyeableAmethystBlock {

    /** A block tag containing all amethyst blocks. */
    public static final TagKey<Block> BUDDING_AMETHYSTS = TagKey.of(Registries.BLOCK.getKey(),
        Astral.getId("budding_amethyst_blocks")
    );
    /** An item tag containing all amethyst blocks. */
    public static final TagKey<Item> BUDDING_AMETHYST_ITEMS = TagKey.of(Registries.ITEM.getKey(),
        Astral.getId("budding_amethyst_blocks")
    );

    public DyeableBuddingAmethystBlock(String rawId, Settings settings, DyeColor color) {
        super(rawId, settings, color);
    }

    private Block getSmallBud() {
        return CustomBlocks.DYEABLE_SMALL_AMETHYST_BUDS.get(this.getColor());
    }

    private Block getMediumBud() {
        return CustomBlocks.DYEABLE_MEDIUM_AMETHYST_BUDS.get(this.getColor());
    }

    private Block getLargeBud() {
        return CustomBlocks.DYEABLE_LARGE_AMETHYST_BUDS.get(this.getColor());
    }

    private Block getCluster() {
        return CustomBlocks.DYEABLE_AMETHYST_CLUSTERS.get(this.getColor());
    }

    @Override
    public CustomBlockItem getItem() {
        return CustomItems.DYEABLE_BUDDING_AMETHYST_BLOCKS.get(this.getColor());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(BuddingAmethystBlock.GROW_CHANCE) != 0) return;

        final Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        final BlockPos blockPos = pos.offset(direction);
        final BlockState blockState = world.getBlockState(blockPos);
        final Block block;

        if (BuddingAmethystBlock.canGrowIn(blockState)) {
            block = this.getSmallBud();
        } else if (blockState.isOf(this.getSmallBud()) && blockState.get(AmethystClusterBlock.FACING) == direction) {
            block = this.getMediumBud();
        } else if (blockState.isOf(this.getMediumBud()) && blockState.get(AmethystClusterBlock.FACING) == direction) {
            block = this.getLargeBud();
        } else if (blockState.isOf(this.getLargeBud()) && blockState.get(AmethystClusterBlock.FACING) == direction) {
            block = this.getCluster();
        } else {
            block = null;
        }

        if (block != null) {
            final BlockState newState = block.getDefaultState()
                .with(DyeableAmethystClusterBlock.FACING, direction)
                .with(DyeableAmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);

            world.setBlockState(blockPos, newState);
        }
    }

    @Override
    public void generate() {
        ModelGenerator.getInstance().generateBlock(g -> g.registerSimpleCubeAll(this));
        TagGenerator.getInstance().generate(BUDDING_AMETHYSTS, b -> b.add(this));
        TagGenerator.getInstance().generate(BUDDING_AMETHYST_ITEMS, b -> b.add(this.getItem()));
        TextureGenerator.getInstance().generate(Registries.BLOCK.getKey(),
            i -> generateTexture(i, "budding_amethyst", this.getColor(), this.getRegistryId())
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
                .group("dyeable_budding_amethysts")
                .input(BUDDING_AMETHYST_ITEMS)
                .input(DyeItem.byColor(this.getColor()))
                .criterion("block", FabricRecipeProvider.conditionsFromTag(BUDDING_AMETHYST_ITEMS))
        );
        LanguageGenerator.getInstance().generate(builder -> {
            final String[] parts = this.getColor().getName().split("_");
            final String value = Arrays.stream(parts)
                .map(s -> StringUtils.capitalize(s) + " ")
                .reduce(String::concat)
                .orElse("Dyed ");

            builder.add(this, value + "Budding Amethyst");
        });
    }

}
