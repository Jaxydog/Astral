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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Provides common functionality for spray items.
 *
 * @author Jaxydog
 * @see SprayBottleItem
 * @see SprayPotionItem
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface Sprayed extends Client, Custom {

    /**
     * The identifier corresponding to a spray item's 'empty' model.
     * <p>
     * This is expected to be used only within the {@link net.minecraft.client.item.ModelPredicateProviderRegistry}.
     *
     * @since 2.0.0
     */
    Identifier EMPTY_MODEL_ID = Astral.getId("empty");

    /**
     * The sound played when spraying a spray item.
     *
     * @since 2.0.0
     */
    SoundContext SPRAY_SOUND = new SoundContext(CustomSoundEvents.SPRAY_BOTTLE_USE, SoundCategory.NEUTRAL);
    /**
     * The sound played when refilling a spray item.
     *
     * @since 2.0.0
     */
    SoundContext REFILL_SOUND = new SoundContext(SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL);

    /**
     * Adds a new behavior to this spray item.
     *
     * @param type The class of the behavior's target type.
     * @param behavior The entity behavior.
     * @param <T> The behavior's target type.
     *
     * @since 2.0.0
     */
    <T extends SprayTarget> void addBehavior(Class<T> type, Behavior<T> behavior);

    /**
     * Returns a list of behaviors for this spray item.
     *
     * @param type The class of the behavior's expected target type.
     * @param <T> The behavior's expected target type.
     *
     * @return A list of behaviors.
     *
     * @since 2.0.0
     */
    <T extends SprayTarget> List<Behavior<T>> getBehaviors(Class<T> type);

    /**
     * Returns the data used within a spray item's model in order to determine its current texture.
     * <p>
     * If the stack is considered 'empty', this method should return {@code 1F}. Otherwise, it should return {@code 0F}.
     * This method may be overwritten to allow for additional numbers between [0, 1], however this range should not be
     * exceeded.
     *
     * @param stack The item stack.
     * @param world The current world.
     * @param entity The entity holding the item.
     * @param seed The randomness seed.
     *
     * @return A number between {@code 0} and {@code 1}.
     *
     * @since 2.0.0
     */
    default float getEmptyModelData(ItemStack stack, World world, LivingEntity entity, int seed) {
        return this.isEmpty(stack) ? 1F : 0F;
    }

    /**
     * Returns the given item stack's usage cooldown in ticks.
     * <p>
     * A value of less than or equal to {@code 0} should be interpreted as having no cooldown.
     * <p>
     * The default implementation returns a duration of {@code 8} ticks.
     *
     * @param stack The item stack.
     *
     * @return The cooldown in ticks.
     *
     * @since 2.0.0
     */
    default int getCooldown(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return 0;

        return 8;
    }

    /**
     * Returns the given item stack's spray duration in ticks.
     * <p>
     * This is used within the implementation of {@link SprayableEntity} to determine reactions.
     * <p>
     * A value of less than or equal to {@code 0} should be interpreted as the item being unable to spray
     * {@link SprayableEntity} instances.
     * <p>
     * The default implementation returns a duration of {@code 40} ticks.
     *
     * @param stack The item stack.
     *
     * @return The duration in ticks.
     *
     * @since 2.0.0
     */
    default int getDuration(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return 0;

        return 40;
    }

    /**
     * Returns the maximum number of times the given item stack may be sprayed before requiring a refill.
     * <p>
     * A value of {@code 0} means that the item may not be sprayed.
     * <p>
     * The default implementation returns the stack's maximum damage amount. If this is overridden,
     * {@link Sprayed#getCharges(ItemStack)} and {@link Sprayed#setCharges(ItemStack, int)} should also be updated.
     *
     * @param stack The item stack.
     *
     * @return The number of charges.
     *
     * @since 2.0.0
     */
    default int getMaxCharges(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return 0;

        return stack.getMaxDamage();
    }

    /**
     * Returns the number of times the given item stack may be sprayed before requiring a refill.
     * <p>
     * A value of {@code 0} means that the item may not be sprayed.
     * <p>
     * The default implementation returns the stack's remaining durability. If this is overridden,
     * {@link Sprayed#getMaxCharges(ItemStack)} and {@link Sprayed#setCharges(ItemStack, int)} should also be updated.
     *
     * @param stack The item stack.
     *
     * @return The number of charges.
     *
     * @since 2.0.0
     */
    default int getCharges(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return 0;

        // Since damage is stored as an incrementing integer, we need to subtract the taken damage from the maximum to
        // get the actual remaining charge count.
        return this.getMaxCharges(stack) - stack.getDamage();
    }

    /**
     * Sets the number of times the given item stack may be sprayed before requiring a refill.
     * <p>
     * The given value will never be outside [0, {@link Sprayed#getMaxCharges(ItemStack)}].
     *
     * @param stack The item stack.
     * @param charges The number of charges.
     *
     * @since 2.0.0
     */
    default void setCharges(ItemStack stack, int charges) {
        if (!(stack.getItem() instanceof Sprayed)) return;

        // Since damage is stored as an incrementing integer, the 'charges' value must be subtracted from 'max' to
        // ensure the correct amount of remaining charges are set. If the 'charges' value is directly passed, the actual
        // value will be incorrect.
        final int max = this.getMaxCharges(stack);
        final int damage = MathHelper.clamp(max - charges, 0, max);

        stack.setDamage(damage);
    }

    /**
     * Returns whether the given item stack contains at least {@code charges} remaining charges and can therefore be
     * sprayed.
     *
     * @param stack The item stack.
     * @param charges The required charges.
     *
     * @return Whether the stack may be sprayed.
     *
     * @since 2.0.0
     */
    default boolean isSprayable(ItemStack stack, int charges) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        return this.getCharges(stack) >= charges;
    }

    /**
     * Returns whether the given item stack contains at least 1 remaining charge and can therefore be sprayed.
     *
     * @param stack The item stack.
     *
     * @return Whether the stack may be sprayed.
     *
     * @since 2.0.0
     */
    default boolean isSprayable(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        return this.isSprayable(stack, 1);
    }

    /**
     * Returns whether the given item stack is filled.
     * <p>
     * The default implementation checks whether the current charges are at least the maximum.
     *
     * @param stack The item stack.
     *
     * @return Whether the stack may be refilled.
     *
     * @since 2.0.0
     */
    default boolean isFilled(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        return this.getCharges(stack) >= this.getMaxCharges(stack);
    }

    /**
     * Returns whether the given item stack may be refilled.
     * <p>
     * The default implementation checks whether the current charges are less than the maximum.
     *
     * @param stack The item stack.
     *
     * @return Whether the stack may be refilled.
     *
     * @since 2.0.0
     */
    default boolean isRefillable(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        return this.getCharges(stack) < this.getMaxCharges(stack);
    }

    /**
     * Returns whether the given item stack is empty.
     * <p>
     * The default implementation just asserts that the remaining charges are less than or equal to zero.
     *
     * @param stack The item stack.
     *
     * @return Whether the stack is empty.
     *
     * @since 2.0.0
     */
    default boolean isEmpty(ItemStack stack) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        return this.getCharges(stack) <= 0;
    }

    /**
     * Returns whether the given item stack may spray the target entity and consume the given charge count.
     *
     * @param source The spray source.
     * @param target The spray target.
     * @param charges The number of charges.
     *
     * @return Whether the target may be sprayed.
     *
     * @since 2.0.0
     */
    default boolean canSpray(Source source, EntityTarget target, int charges) {
        if (!this.isSprayable(source.stack(), charges)) return false;

        // Return true if the stack has any valid behaviors.
        if (this.getBehaviors(EntityTarget.class).stream().anyMatch(b -> b.predicate().test(source, target))) {
            return true;
        }

        // Return true if the target has any valid `astral:action_when_sprayed` powers.
        if (PowerHolderComponent.getPowers(target.target(), ActionWhenSprayedPower.class)
            .stream()
            .anyMatch(p -> p.canBeSprayed(source.actor(), source.stack()))) {
            return true;
        }

        // Return true if the entity has any valid `astral:action_on_sprayed` powers.
        if (PowerHolderComponent.getPowers(source.actor(), ActionOnSprayPower.class)
            .stream()
            .anyMatch(p -> p.canSprayEntity(source.stack(), target.target()))) {
            return true;
        }

        return target.target() instanceof SprayableEntity && this.getDuration(source.stack()) > 0;
    }

    /**
     * Returns whether the given item stack may spray the target block and consume the given charge count.
     *
     * @param source The spray source.
     * @param target The spray target.
     * @param charges The number of charges.
     *
     * @return Whether the block may be sprayed.
     *
     * @since 2.0.0
     */
    default boolean canSpray(Source source, BlockTarget target, int charges) {
        if (!this.isSprayable(source.stack(), charges)) return false;

        // Return true if the entity has any valid `astral:action_on_sprayed` powers.
        if (PowerHolderComponent.getPowers(source.actor(), ActionOnSprayPower.class)
            .stream()
            .anyMatch(p -> p.canSprayBlock(source.stack(), target.world(), target.pos()))) {
            return true;
        }

        // Return true if the stack has any valid behaviors.
        return this.getBehaviors(BlockTarget.class).stream().anyMatch(b -> b.predicate().test(source, target));
    }

    /**
     * Runs actions on a sprayed target, returning a result.
     *
     * @param source The spray source.
     * @param target The spray target.
     * @param silent Whether the spray sound should be skipped.
     *
     * @return The spray result.
     *
     * @since 2.0.0
     */
    default SprayResult onSpray(Source source, EntityTarget target, boolean silent) {
        final List<Runnable> actions = new ObjectArrayList<>();
        int charges = 0;

        final List<Behavior<EntityTarget>> behaviors = this.getBehaviors(EntityTarget.class);

        // Sort by behavior priority in descending order.
        behaviors.sort(Comparator.comparingInt(Behavior<EntityTarget>::priority).reversed());

        for (final Behavior<EntityTarget> behavior : behaviors) {
            if (!behavior.predicate().test(source, target)) continue;

            actions.add(() -> behavior.action().accept(source, target));
            charges = Math.max(charges, behavior.charges());

            if (behavior.cancelling()) break;
        }

        final List<ActionWhenSprayedPower> targetPowers = PowerHolderComponent.getPowers(target.target(),
            ActionWhenSprayedPower.class
        );

        // Sort by power priority in descending order.
        targetPowers.sort(Comparator.comparingInt(ActionWhenSprayedPower::getPriority).reversed());

        for (final ActionWhenSprayedPower power : targetPowers) {
            if (!power.canBeSprayed(source.actor(), source.stack())) continue;

            actions.add(() -> power.onSprayed(source.actor(), source.stack()));
            charges = Math.max(charges, power.getCharges());
        }

        final List<ActionOnSprayPower> actorPowers = PowerHolderComponent.getPowers(source.actor(),
            ActionOnSprayPower.class
        );

        // Sort by power priority in descending order.
        actorPowers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

        for (final ActionOnSprayPower power : actorPowers) {
            if (!power.canSprayEntity(source.stack(), target.target())) continue;

            actions.add(() -> power.onSprayEntity(source.stack(), target.target()));
            charges = Math.max(charges, power.getCharges());
        }

        if (target.target() instanceof final SprayableEntity sprayable) {
            final int duration = this.getDuration(source.stack());

            // Only spray on the server-side. This avoids weird behaviors when called on the client.
            if (duration > 0 && !target.target().getWorld().isClient()) {
                actions.add(() -> sprayable.astral$setSprayed(source.actor(), duration));

                charges = Math.max(charges, sprayable.astral$getSprayCharges());
            }
        }

        return new SprayResult(charges, source.position(), actions, silent);
    }

    /**
     * Runs actions on a sprayed block, returning a result.
     *
     * @param source The spray source.
     * @param target The spray target.
     * @param silent Whether the spray sound should be skipped.
     *
     * @return The spray result.
     *
     * @since 2.0.0
     */
    default SprayResult onSpray(Source source, BlockTarget target, boolean silent) {
        final List<Runnable> actions = new ObjectArrayList<>();
        int charges = 0;

        final List<Behavior<BlockTarget>> behaviors = this.getBehaviors(BlockTarget.class);

        // Sort by behavior priority in descending order.
        behaviors.sort(Comparator.comparingInt(Behavior<BlockTarget>::priority).reversed());

        for (final Behavior<BlockTarget> behavior : behaviors) {
            if (!behavior.predicate().test(source, target)) continue;

            actions.add(() -> behavior.action().accept(source, target));
            charges = Math.max(charges, behavior.charges());

            if (behavior.cancelling()) break;
        }

        final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(source.actor(),
            ActionOnSprayPower.class
        );

        // Sort by power priority in descending order.
        powers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

        for (final ActionOnSprayPower power : powers) {
            if (!power.canSprayBlock(source.stack(), target.world(), target.pos())) continue;

            actions.add(() -> power.onSprayBlock(source.stack(), target.world(), target.pos(), target.side()));
            charges = Math.max(charges, power.getCharges());
        }

        return new SprayResult(charges, source.position(), actions, silent);
    }

    /**
     * Sprays a target entity.
     * <p>
     * This method should not be overwritten directly, instead you should prefer overwriting
     * {@link Sprayed#onSpray(Source, EntityTarget, boolean)}.
     *
     * @param stack The item stack.
     * @param actor The actor entity.
     * @param from The source position.
     * @param target The target entity.
     * @param silent Whether the spray sound should be skipped.
     *
     * @return Whether the target was sprayed.
     *
     * @since 2.0.0
     */
    default boolean spray(ItemStack stack, @Nullable LivingEntity actor, Vec3d from, Entity target, boolean silent) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        final Source source = new Source(stack, actor, from);
        final EntityTarget sprayTarget = new EntityTarget(target);

        return this.spray(source, target.getWorld(), this.onSpray(source, sprayTarget, silent));
    }

    /**
     * Sprays a block.
     * <p>
     * This method should not be overwritten directly, instead you should prefer overwriting
     * {@link Sprayed#onSpray(Source, BlockTarget, boolean)}.
     *
     * @param stack The item stack.
     * @param actor The actor entity.
     * @param from The source position.
     * @param world The current world.
     * @param pos The block position.
     * @param side The sprayed side.
     * @param silent Whether the spray sound should be skipped.
     *
     * @return Whether the target was sprayed.
     *
     * @since 2.0.0
     */
    default boolean spray(
        ItemStack stack,
        @Nullable LivingEntity actor,
        Vec3d from,
        World world,
        BlockPos pos,
        Direction side,
        boolean silent
    ) {
        if (!(stack.getItem() instanceof Sprayed)) return false;

        final Source source = new Source(stack, actor, from);
        final BlockTarget target = new BlockTarget(world, pos, side);

        return this.spray(source, world, this.onSpray(source, target, silent));
    }

    /**
     * Sprays the item.
     * <p>
     * This should never be called directly. Instead, you want either
     * {@link Sprayed#spray(ItemStack, LivingEntity, Vec3d, Entity, boolean)} or
     * {@link Sprayed#spray(ItemStack, LivingEntity, Vec3d, World, BlockPos, Direction, boolean)}.
     *
     * @param source The spray source.
     * @param result The result of another spray method.
     *
     * @return Whether the result was acted upon.
     *
     * @since 2.0.0
     */
    default boolean spray(Source source, World world, SprayResult result) {
        if (result.charges() == 0 || result.actions().isEmpty()) return false;
        if (!this.isSprayable(source.stack(), result.charges())) return false;

        // Execute the result's stored actions.
        result.accept(source.stack(), (s, charges) -> {
            // Don't consume if the actor is in creative mode.
            if (source.actor() instanceof final PlayerEntity player && player.isCreative()) return;

            if (source.actor() instanceof final ServerPlayerEntity player) {
                source.stack().damage(charges, world.getRandom(), player);
            } else {
                source.stack().damage(charges, world.getRandom(), null);
            }
        });

        if (source.actor() instanceof final PlayerEntity player) {
            player.incrementStat(Stats.USED.getOrCreateStat(source.stack().getItem()));
            // Negative values will set the cooldown to be in the past, effectively ignoring them.
            player.getItemCooldownManager().set(source.stack().getItem(), this.getCooldown(source.stack()));
        }

        if (result.silent()) return true;

        if (source.actor() == null) {
            SPRAY_SOUND.play(world, source.position());
        } else if (!source.actor().isSilent()) {
            SPRAY_SOUND.play(world, source.actor());
        }

        return true;
    }

    /**
     * Refills the spray item using the given context.
     *
     * @param source The refill source.
     * @param context The refill context.
     *
     * @since 2.0.0
     */
    default void refill(Source source, RefillContext context) {
        if (this.isFilled(source.stack())) return;

        final int charges = this.getCharges(source.stack()) + context.charges();

        this.setCharges(source.stack(), charges);

        if (source.actor() == null) {
            REFILL_SOUND.play(context.world(), source.position());

            final BlockPos pos = BlockPos.ofFloored(source.position());
            final BlockState state = context.world().getBlockState(pos);

            context.world().emitGameEvent(GameEvent.FLUID_PICKUP, source.position(), Emitter.of(state));
        } else if (!source.actor().isSilent()) {
            REFILL_SOUND.play(context.world(), source.actor());

            context.world().emitGameEvent(source.actor(), GameEvent.FLUID_PICKUP, source.position());
        }
    }

    @Override
    default void registerClient() {
        // Tells the game to render a different model depending on the value provided by `getEmptyModelData`.
        ModelPredicateProviderRegistry.register(this.asItem(), EMPTY_MODEL_ID, this::getEmptyModelData);
    }

    /**
     * Describes the source of a spray item interaction.
     *
     * @param stack The item stack.
     * @param actor The actor entity.
     * @param position The source position. If {@code actor} is non-null, this is their position. Otherwise, this is the
     * * center of the block that is being sprayed from.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    record Source(ItemStack stack, @Nullable LivingEntity actor, Vec3d position) {

        /**
         * Describes the source of a spray item interaction.
         *
         * @param stack The item stack.
         * @param actor The actor entity.
         *
         * @since 2.0.0
         */
        public Source(ItemStack stack, @NotNull LivingEntity actor) {
            this(stack, actor, actor.getPos());
        }

    }

    /**
     * Describes the target of a spray invocation.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    interface SprayTarget {

        /**
         * Returns the distance of this target from the spray source.
         *
         * @param source The spray source.
         *
         * @return The distance.
         *
         * @since 2.0.0
         */
        double distance(Source source);

    }

    /**
     * Describes the target entity of a spray invocation.
     *
     * @param target The target entity.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    record EntityTarget(Entity target) implements SprayTarget {

        @Override
        public double distance(Source source) {
            return this.target().getPos().distanceTo(source.position());
        }

    }

    /**
     * Describes the target block of a spray invocation.
     *
     * @param world The current world.
     * @param pos The block position.
     * @param side The sprayed side.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    record BlockTarget(World world, BlockPos pos, Direction side) implements SprayTarget {

        /**
         * Returns the block target's block state.
         *
         * @return The block's state.
         *
         * @since 2.0.0
         */
        public BlockState state() {
            return this.world().getBlockState(this.pos());
        }

        @Override
        public double distance(Source source) {
            return pos.toCenterPos().distanceTo(source.position());
        }

    }

    /**
     * Describes and implements a behavior when spraying an object.
     *
     * @param predicate A predicate that tests whether a given state is valid.
     * @param action The action to execute if the state is valid.
     * @param charges The charges to consume when the action is executed.
     * @param priority The priority of the action's execution.
     * @param cancelling Whether subsequent actions should be cancelled.
     * @param <T> The expected target type.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    record Behavior<T extends SprayTarget>(
        BiPredicate<Source, T> predicate, BiConsumer<Source, T> action, int charges, int priority, boolean cancelling
    ) {

        /**
         * Describes and implements a behavior when spraying an object.
         *
         * @param predicate A predicate that tests whether a given state is valid.
         * @param action The action to execute if the state is valid.
         * @param charges The charges to consume when the action is executed.
         * @param priority The priority of the action's execution.
         *
         * @since 2.0.0
         */
        public Behavior(
            BiPredicate<Source, T> predicate, BiConsumer<Source, T> action, int charges, int priority
        ) {
            this(predicate, action, charges, priority, false);
        }

        /**
         * Describes and implements a behavior when spraying an object.
         *
         * @param predicate A predicate that tests whether a given state is valid.
         * @param action The action to execute if the state is valid.
         * @param charges The charges to consume when the action is executed.
         *
         * @since 2.0.0
         */
        public Behavior(BiPredicate<Source, T> predicate, BiConsumer<Source, T> action, int charges) {
            this(predicate, action, charges, 0);
        }

    }

    /**
     * The result of spraying an object.
     * <p>
     * If the result is valid, the {@link SprayResult#accept(ItemStack, BiConsumer)} method should be invoked.
     *
     * @param charges The charges that should be consumed.
     * @param position The position that the bottle was sprayed from.
     * @param actions The actions that should be executed.
     * @param silent Whether to skip playing the spray sound.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    record SprayResult(int charges, Vec3d position, List<Runnable> actions, boolean silent) {

        /**
         * Executes the result's stored actions, consuming charges from the given stack.
         *
         * @param stack The item stack.
         * @param consume The charge consumption method.
         */
        public void accept(ItemStack stack, BiConsumer<ItemStack, Integer> consume) {
            if (!(stack.getItem() instanceof Sprayed)) return;

            this.actions().forEach(Runnable::run);

            consume.accept(stack, this.charges());
        }

    }

    /**
     * Describes a spray item refilling context.
     *
     * @param world The current world.
     * @param pos The block position.
     * @param charges The refilled charges.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    record RefillContext(World world, BlockPos pos, int charges) {}

}
