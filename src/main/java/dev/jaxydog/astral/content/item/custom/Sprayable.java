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

package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.item.Custom;
import dev.jaxydog.astral.content.power.custom.ActionOnSprayPower;
import dev.jaxydog.astral.content.power.custom.ActionWhenSprayedPower;
import dev.jaxydog.astral.content.sound.CustomSoundEvents;
import dev.jaxydog.astral.content.sound.SoundContext;
import dev.jaxydog.astral.register.Registered.Client;
import dev.jaxydog.astral.utility.injected.SprayableEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * Provides common functionality for all sprayable items.
 *
 * @author Jaxydog
 * @see SprayBottleItem
 * @see SprayPotionItem
 */
@SuppressWarnings("unused")
public interface Sprayable extends Client, Custom {

    /**
     * The identifier corresponding to a sprayable item's empty model.
     * <p>
     * This is expected to be used solely within the {@link ModelPredicateProviderRegistry}.
     */
    Identifier EMPTY_MODEL_ID = Astral.getId("empty");
    /** The sound played when a sprayable item is sprayed. */
    SoundContext SPRAY_SOUND = new SoundContext(CustomSoundEvents.SPRAY_BOTTLE_USE, SoundCategory.NEUTRAL);
    /** The sound played when a sprayable item is refilled. */
    SoundContext FILL_SOUND = new SoundContext(SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL);

    /**
     * Returns the maximum number of times the given stack may be sprayed before refilling.
     * <p>
     * A value of {@code 0} means that the item may not be sprayed.
     *
     * @param stack The item stack.
     *
     * @return The number of charges.
     */
    default int getMaximumCharges(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayable)) return 0;

        return stack.getMaxDamage();
    }

    /**
     * Returns the remaining number of times the given stack may be sprayed before refilling.
     * <p>
     * A value less than or equal to {@code 0} means that the item has been depleted.
     *
     * @param stack The item stack.
     *
     * @return The number of charges.
     */
    default int getRemainingCharges(ItemStack stack) {
        // Damage within item stacks is stored as an incrementing integer as part of the associated NBT data.
        // Under normal circumstances, when this integer is greater than or equal to the maximum allowed damage value,
        // the item is considered to be broken and is removed from the inventory.
        // Sprayed items (conventionally) do not do this. Instead, they interpret this damage field as a sort of
        // "expended" counter, which tracks the total number of charges that have been consumed. They *also* use the
        // item's maximum damage bound as a means of determining the total number of allowed charges. Because of
        // this, sprayable items are able to interpret the game's natural damage tracking as a way of tracking charges.
        //
        // TLDR; `getDamage` tracks the number of charges consumed, so `max - damage` returns the remaining charges.
        return this.getMaximumCharges(stack) - stack.getDamage();
    }

    /**
     * Sets the remaining number of times the given stack may be sprayed before refilling.
     *
     * @param stack The item stack.
     * @param charges The expected number of charges.
     */
    default void setRemainingCharges(ItemStack stack, int charges) {
        // Since damage within stacks is stored as an incrementing integer, setting it to `max - charges` will allow
        // exactly `charges` more uses before becoming empty.
        // We can clamp the remainder to `0` in the event that a value larger that the possible maximum is provided,
        // and we clamp the charges to `0` to prevent a negative value being provided.
        stack.setDamage(Math.max(0, this.getMaximumCharges(stack) - Math.max(0, charges)));
    }

    /**
     * Returns whether the given item stack represents an empty spray item.
     *
     * @param stack The item stack.
     *
     * @return If the stack is empty.
     */
    default boolean isEmpty(ItemStack stack) {
        return this.getRemainingCharges(stack) == 0;
    }

    /**
     * Returns whether the given stack may be sprayed while consuming the specified amount of charges.
     *
     * @param stack The item stack.
     * @param charges The number of charges required.
     *
     * @return Whether the stack may be sprayed.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canSpray(ItemStack stack, int charges) {
        return this.getRemainingCharges(stack) >= charges;
    }

    /**
     * Returns whether the given stack may spray the target entity.
     *
     * @param stack The item stack.
     * @param source The entity holding the stack.
     * @param target The target entity.
     * @param charges The number of charges required.
     *
     * @return Whether the entity may be sprayed.
     */
    default boolean canSpray(ItemStack stack, @Nullable LivingEntity source, Entity target, int charges) {
        if (!this.canSpray(stack, charges)) return false;
        if (!PowerHolderComponent.getPowers(target, ActionWhenSprayedPower.class).isEmpty()) return true;

        if (source != null) {
            final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(source, ActionOnSprayPower.class);

            if (powers.stream().anyMatch(p -> p.canSprayEntity(stack, target))) return true;
        }

        return target instanceof SprayableEntity;
    }

    /**
     * Returns whether the given stack may spray the target block.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param pos The block position.
     * @param source The entity holding the stack.
     * @param charges The number of charges required.
     *
     * @return Whether the entity may be sprayed.
     */
    default boolean canSpray(ItemStack stack, World world, @Nullable LivingEntity source, BlockPos pos, int charges) {
        if (!this.canSpray(stack, charges)) return false;

        if (source != null) {
            final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(source, ActionOnSprayPower.class);

            return powers.stream().anyMatch(p -> p.canSprayBlock(stack, world, pos));
        }

        return false;
    }

    /**
     * Returns whether the given stack may be filled.
     *
     * @param stack The item stack.
     *
     * @return Whether the stack may be filled.
     */
    default boolean canFill(ItemStack stack) {
        return this.getRemainingCharges(stack) < this.getMaximumCharges(stack);
    }

    /**
     * Returns whether the given stack has remaining unused charges.
     *
     * @param stack The item stack.
     *
     * @return Whether charges remain.
     */
    default boolean hasCharges(ItemStack stack) {
        return this.getRemainingCharges(stack) > 0;
    }

    /**
     * Returns the given sprayable item stack's usage cooldown in ticks.
     * <p>
     * By default, this method returns {@code 8}.
     *
     * @param stack The item stack.
     *
     * @return The cooldown in ticks.
     */
    default int getSprayCooldown(ItemStack stack) {
        return 8;
    }

    /**
     * Returns the duration in ticks that {@link SprayableEntity} instances are sprayed for.
     * <p>
     * By default, this method returns {@code 40}.
     *
     * @param stack The item stack.
     *
     * @return The duration in ticks.
     */
    default int getSprayDuration(ItemStack stack) {
        return 40;
    }

    /**
     * This method is called when the item is sprayed.
     * <p>
     * This runs on both the client and the server. To ensure proper functionality, the environment should be checked
     * using the {@link World#isClient()} method.
     * <p>
     * This method does not handle spray actions itself. It should instead be used for reacting to spray events.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param entity The entity holding the item.
     * @param charges The charges consumed.
     *
     * @see #invokeSpray(ItemStack, World, LivingEntity, int)
     */
    default void onSpray(ItemStack stack, World world, @Nullable LivingEntity entity, int charges) {}

    /**
     * This method is called when the item is refilled.
     * <p>
     * This runs on both the client and the server. To ensure proper functionality, the environment should be checked
     * using the {@link World#isClient()} method.
     * <p>
     * This method does not handle fill actions itself. It should instead be used for reacting to fill events.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param pos The position of the block used to refill.
     * @param entity The entity holding the item.
     * @param charges The charges refilled.
     *
     * @see #invokeFill(ItemStack, World, BlockPos, LivingEntity, int)
     */
    default void onFill(ItemStack stack, World world, BlockPos pos, @Nullable LivingEntity entity, int charges) {}

    /**
     * This method is called when the item is emptied.
     * <p>
     * This runs on both the client and the server. To ensure proper functionality, the environment should be checked
     * using the {@link World#isClient()} method.
     * <p>
     * This method does not handle empty actions itself. It should instead be used for reacting to empty events.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param entity The entity holding the item.
     *
     * @see #invokeSpray(ItemStack, World, LivingEntity, int)
     */
    default void onEmpty(ItemStack stack, World world, @Nullable LivingEntity entity) {}

    /**
     * Invokes the item's spray callbacks, consuming charges and updating the entity if provided.
     * <p>
     * This method should be called when spraying an entity.
     * <p>
     * Unlike {@link #onSpray(ItemStack, World, LivingEntity, int)}, this handles the item's spray action.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param entity The entity holding the item.
     * @param target The entity being sprayed.
     * @param charges The charges consumed.
     */
    default void invokeSpray(ItemStack stack, World world, @Nullable LivingEntity entity, Entity target, int charges) {
        if (!this.canSpray(stack, entity, target, charges)) return;

        if (target instanceof final SprayableEntity sprayable && sprayable.astral$canSpray()) {
            sprayable.astral$setSprayed(entity, this.getSprayDuration(stack));
        }

        final List<ActionWhenSprayedPower> targetPowers = PowerHolderComponent.getPowers(target,
            ActionWhenSprayedPower.class
        );

        // Sorts the powers by their priority in descending order.
        targetPowers.sort(Comparator.comparingInt(ActionWhenSprayedPower::getPriority).reversed());

        for (final ActionWhenSprayedPower power : targetPowers) {
            if (!power.canBeSprayed(entity, stack)) continue;

            if (power.onSprayed(entity, stack)) {
                charges = Math.max(charges, power.getCharges());
            }
        }

        if (entity != null) {
            final List<ActionOnSprayPower> sourcePowers = PowerHolderComponent.getPowers(entity,
                ActionOnSprayPower.class
            );

            // Sorts the powers by their priority in descending order.
            sourcePowers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

            for (final ActionOnSprayPower power : sourcePowers) {
                if (!power.canSprayEntity(stack, target)) continue;

                if (power.onSprayEntity(stack, target)) {
                    charges = Math.max(charges, power.getCharges());
                }
            }
        }

        this.invokeSpray(stack, world, entity, charges);
    }

    /**
     * Invokes the item's spray callbacks, consuming charges and updating the entity if provided.
     * <p>
     * This method should be called when spraying a block.
     * <p>
     * Unlike {@link #onSpray(ItemStack, World, LivingEntity, int)}, this handles the item's spray action.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param entity The entity holding the item.
     * @param pos The position of the block being sprayed.
     * @param side The side clicked.
     * @param charges The charges consumed.
     */
    default void invokeSpray(
        ItemStack stack, World world, @Nullable LivingEntity entity, BlockPos pos, Direction side, int charges
    ) {
        if (!this.canSpray(stack, world, entity, pos, charges)) return;

        if (entity != null) {
            final List<ActionOnSprayPower> sourcePowers = PowerHolderComponent.getPowers(entity,
                ActionOnSprayPower.class
            );

            // Sorts the powers by their priority in descending order.
            sourcePowers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

            for (final ActionOnSprayPower power : sourcePowers) {
                if (!power.canSprayBlock(stack, world, pos)) continue;

                if (power.onSprayBlock(stack, world, pos, side)) {
                    charges = Math.max(charges, power.getCharges());
                }
            }
        }

        this.invokeSpray(stack, world, entity, charges);
    }

    /**
     * Invokes the item's spray callbacks, consuming charges and updating the entity if provided.
     * <p>
     * Unlike {@link #onSpray(ItemStack, World, LivingEntity, int)}, this handles the item's spray action.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param entity The entity holding the item.
     * @param charges The charges consumed.
     */
    default void invokeSpray(ItemStack stack, World world, @Nullable LivingEntity entity, int charges) {
        if (!(stack.getItem() instanceof Sprayable) || !this.hasCharges(stack)) return;

        // If the entity is null, or the entity is a non-player, or they are not in creative mode.
        if (entity == null || !(entity instanceof final PlayerEntity player && player.isCreative())) {
            if (entity instanceof final ServerPlayerEntity player) {
                stack.damage(charges, world.getRandom(), player);
            } else {
                stack.damage(charges, world.getRandom(), null);
            }
        }

        if (entity instanceof final PlayerEntity player) {
            player.getItemCooldownManager().set(stack.getItem(), this.getSprayCooldown(stack));
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }

        if (entity != null && !entity.isSilent()) SPRAY_SOUND.play(world, entity);

        if (!this.hasCharges(stack)) this.onEmpty(stack, world, entity);

        this.onSpray(stack, world, entity, charges);
    }

    /**
     * Invokes the item's fill callbacks, replenishing charges and updating the entity if provided.
     * <p>
     * Unlike {@link #onFill(ItemStack, World, BlockPos, LivingEntity, int)}, this handles the item's fill action.
     *
     * @param stack The item stack.
     * @param world The world.
     * @param pos The position of the block used to refill.
     * @param entity The entity holding the item.
     * @param charges The charges restored.
     */
    default void invokeFill(ItemStack stack, World world, BlockPos pos, @Nullable LivingEntity entity, int charges) {
        if (!(stack.getItem() instanceof Sprayable) || !this.canFill(stack)) return;

        final int totalCharges = this.getRemainingCharges(stack) + charges;

        this.setRemainingCharges(stack, totalCharges);

        if (entity != null) {
            FILL_SOUND.play(world, entity);

            world.emitGameEvent(entity, GameEvent.FLUID_PICKUP, pos);
        }

        this.onFill(stack, world, pos, entity, charges);
    }

    /**
     * Returns the data used in the sprayable item's model that determines the texture to use.
     * <p>
     * If the stack represents an empty item, this method should return {@code 1F}. Otherwise, if the stack represents a
     * non-empty item, this method should return {@code 0F} (or any other value expected by its model).
     * <p>
     * The default behavior is to always return either {@code 0F} or {@code 1F}.
     *
     * @param stack The item stack.
     * @param world The world instance.
     * @param entity The entity holding the item.
     * @param seed The randomness seed.
     *
     * @return The data used to determine the item's texture.
     */
    default float getEmptyModelData(ItemStack stack, World world, LivingEntity entity, int seed) {
        return this.hasCharges(stack) ? 0F : 1F;
    }

    @Override
    default void registerClient() {
        // Tells the game to render a different model depending on the value provided by `getEmptyModelData`.
        ModelPredicateProviderRegistry.register(this.asItem(), EMPTY_MODEL_ID, this::getEmptyModelData);
    }

}
