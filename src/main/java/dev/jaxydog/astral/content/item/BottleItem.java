/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Icepenguin
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

package dev.jaxydog.astral.content.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An extension of an {@link AstralItem} that returns a glass bottle to the entity when consumed.
 * <p>
 * This is one of the various instances of already-provided wrapper classes for commonly used types.
 * <p>
 * In future code, you should prefer to extend this class over {@link AstralItem} if at all possible.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 * @see Custom
 */
public class BottleItem extends AstralItem {

    /**
     * Creates a new bowl item using the given settings.
     * <p>
     * If the {@code preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     */
    public BottleItem(String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup) {
        super(path, settings, preferredGroup);
    }

    /**
     * Creates a new bowl item using the given settings.
     * <p>
     * This item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     */
    public BottleItem(String path, Settings settings) {
        super(path, settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        final int count = stack.getCount();
        final ItemStack consumed = super.finishUsing(stack, world, user);

        user.emitGameEvent(GameEvent.DRINK);

        if (user instanceof final PlayerEntity player) {
            // If the user is a player, increment stats & trigger criteria.
            player.incrementStat(Stats.USED.getOrCreateStat(this));

            if (player instanceof final ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, consumed);
            }

            // If non-empty and an item was consumed.
            if (!consumed.isEmpty() && consumed.getCount() < count) {
                player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
            }
        }

        // Otherwise, give them a bottle if the stack is empty.
        if (consumed.isEmpty()) return Items.GLASS_BOTTLE.getDefaultStack();

        return consumed;
    }

}
