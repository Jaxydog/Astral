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
import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.datagen.ModelGenerator;
import dev.jaxydog.datagen.TagGenerator;
import dev.jaxydog.register.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    /**
     * Returns this block's associated item.
     *
     * @return The associated item.
     */
    public Item getItem() {
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
    }

}
