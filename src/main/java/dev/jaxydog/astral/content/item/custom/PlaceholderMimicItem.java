/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023–2024 Jaxydog
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

import com.google.common.collect.Multimap;
import dev.jaxydog.astral.content.item.Customized;
import dev.jaxydog.astral.register.RegisteredMap;
import dev.jaxydog.astral.utility.injected.AstralItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Implements a placeholder item, or an item that exists to mimic pre-existing items.
 *
 * @author Jaxydog
 * @since 1.4.0
 */
public class PlaceholderMimicItem extends PlaceholderItem {

    /**
     * Returns the item to be mimicked.
     *
     * @since 1.4.0
     */
    private final Supplier<Item> item;

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     *
     * @since 2.0.0
     */
    public PlaceholderMimicItem(
        String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup, Supplier<Item> item
    ) {
        super(path, settings, preferredGroup);

        this.item = item;
    }

    /**
     * Creates a new item using the given settings.
     * <p>
     * This item will be added to the default item group.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     *
     * @since 1.4.0
     */
    public PlaceholderMimicItem(String path, Settings settings, Supplier<Item> item) {
        super(path, settings);

        this.item = item;
    }

    /**
     * Returns the mimicked item.
     *
     * @return The item.
     *
     * @since 1.4.0
     */
    public Item getItem() {
        return this.item.get();
    }

    /**
     * Returns the mimicked item depending on the provided stack.
     * <p>
     * The default implementation just calls {@link #getItem()}.
     *
     * @param stack The item stack.
     *
     * @return The item.
     *
     * @since 1.4.0
     */
    public Item getItem(ItemStack stack) {
        return this.getItem();
    }

    @Override
    public EquipmentSlot getSlotType() {
        if (this.getItem() instanceof final Equipment equipment) {
            return equipment.getSlotType();
        } else {
            return super.getSlotType();
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        this.getItem(stack).usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        this.getItem(entity.getStack()).onItemEntityDestroyed(entity);
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        this.getItem().postProcessNbt(nbt);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return this.getItem(miner.getActiveItem()).canMine(state, world, pos, miner);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return this.getItem(context.getStack()).useOnBlock(context);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return this.getItem(stack).getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.getItem(user.getStackInHand(hand)).use(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        return this.getItem(stack).finishUsing(stack, world, user);
    }

    @Override
    public boolean isDamageable() {
        return this.getItem().isDamageable();
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return this.getItem(stack).isItemBarVisible(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return this.getItem(stack).getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return this.getItem(stack).getItemBarColor(stack);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        return this.getItem(stack).onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(
        ItemStack stack, ItemStack held, Slot slot, ClickType type, PlayerEntity player, StackReference cursor
    ) {
        return this.getItem(stack).onClicked(stack, held, slot, type, player, cursor);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return this.getItem(stack).postHit(stack, target, attacker);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return this.getItem(stack).postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        return this.getItem().isSuitableFor(state);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return this.getItem(stack).useOnEntity(stack, user, entity, hand);
    }

    @Override
    public boolean isNbtSynced() {
        return this.getItem().isNbtSynced();
    }

    @Override
    public boolean hasRecipeRemainder() {
        return this.getItem().hasRecipeRemainder();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        this.getItem(stack).inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        this.getItem(stack).onCraft(stack, world, player);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return this.getItem(stack).getUseAction(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return this.getItem(stack).getMaxUseTime(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        this.getItem(stack).onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return this.getItem(stack).getTooltipData(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.getItem(stack).hasGlint(stack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.getItem(stack).getRarity(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.getItem(stack).isEnchantable(stack);
    }

    @Override
    public int getEnchantability() {
        return this.getItem().getEnchantability();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.getItem(stack).canRepair(stack, ingredient);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return this.getItem().getAttributeModifiers(slot);
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return this.getItem(stack).isUsedOnRelease(stack);
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public ItemStack getDefaultStack() {
        // This effectively copies the mimicked item's preferred default stack.
        final ItemStack stack = this.getItem().getDefaultStack();

        // Then replaces the stack's item with the current instance.
        ((AstralItemStack) stack).astral$setItem(this);

        return stack;
    }

    @Override
    public boolean isFood() {
        return this.getItem().isFood();
    }

    @Override
    public FoodComponent getFoodComponent() {
        return this.getItem().getFoodComponent();
    }

    @Override
    public SoundEvent getDrinkSound() {
        return this.getItem().getDrinkSound();
    }

    @Override
    public SoundEvent getEatSound() {
        return this.getItem().getEatSound();
    }

    @Override
    public boolean isFireproof() {
        return this.getItem().isFireproof();
    }

    @Override
    public boolean damage(DamageSource source) {
        return this.getItem().damage(source);
    }

    @Override
    public boolean canBeNested() {
        return this.getItem().canBeNested();
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.getItem().getRequiredFeatures();
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        this.getItem(stack).appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return this.getItem().isEnabled(enabledFeatures);
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return this.getItem(oldStack).allowNbtUpdateAnimation(player, hand, oldStack, newStack);
    }

    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return this.getItem(oldStack).allowContinuingBlockBreaking(player, oldStack, newStack);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(
        ItemStack stack, EquipmentSlot slot
    ) {
        return this.getItem(stack).getAttributeModifiers(stack, slot);
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        return this.getItem(stack).isSuitableFor(stack, state);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return this.getItem(stack).getRecipeRemainder(stack);
    }

    @Override
    public SoundEvent getEquipSound() {
        if (this.getItem() instanceof final Equipment equipment) {
            return equipment.getEquipSound();
        } else {
            return super.getEquipSound();
        }
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
        // If the mimicked item has an implementation for this, prefer that implementation.
        if (this.getItem(user.getStackInHand(hand)) instanceof final Equipment equipment) {
            final TypedActionResult<ItemStack> result = equipment.equipAndSwap(item, world, user, hand);

            // Ensure the item is retained when calling the super method. This is unlikely to break functionality, but
            // it is still possible.
            // If mimicking an item that overrides this method, you should ideally create an extension that provides the
            // expected behavior.
            ((AstralItemStack) result.getValue()).astral$setItem(this);

            return equipment.equipAndSwap(item, world, user, hand);
        } else {
            return super.equipAndSwap(item, world, user, hand);
        }
    }

    @Override
    public Optional<Integer> getCustomModelData(ItemStack stack) {
        // If the mimicked item has an implementation for this, prefer that implementation.
        if (this.getItem(stack) instanceof final Customized customized) {
            return customized.getCustomModelData(stack);
        } else {
            return super.getCustomModelData(stack);
        }
    }

    @Override
    public void setCustomModelData(ItemStack stack, int data) {
        // If the mimicked item has an implementation for this, prefer that implementation.
        if (this.getItem(stack) instanceof final Customized customized) {
            customized.setCustomModelData(stack, data);
        } else {
            super.setCustomModelData(stack, data);
        }
    }

    /**
     * A group of mimicking placeholder items.
     * <p>
     * This is most useful for bulk declaration, for example when defining armor items or dye items.
     *
     * @author Jaxydog
     * @since 1.4.0
     */
    public static class Group extends RegisteredMap<Item, PlaceholderMimicItem> {

        /**
         * The mimicked items.
         *
         * @since 2.0.0
         */
        private final Set<Item> items;

        /**
         * Creates a new group of mimicking items.
         * <p>
         * If the {@code #preferredGroup} supplier is {@code null}, the items will not be added to any item groups.
         *
         * @param path The item's base identifier path.
         * @param settings The item's settings.
         * @param preferredGroup The item's preferred item group.
         * @param items The items to be mimicked.
         *
         * @since 2.0.0
         */
        public Group(
            String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup, Item... items
        ) {
            super(path, (id, item) -> new PlaceholderMimicItem(id, settings, preferredGroup, () -> item));

            this.items = Set.of(items);
        }

        /**
         * Creates a new group of mimicking items.
         * <p>
         * The items will be added to the default item group.
         *
         * @param path The item's base identifier path.
         * @param settings The item's settings.
         * @param items The items to be mimicked.
         *
         * @since 1.4.0
         */
        public Group(String path, Settings settings, Item... items) {
            super(path, (id, item) -> new PlaceholderMimicItem(id, settings, () -> item));

            this.items = Set.of(items);
        }

        @Override
        protected int compareKeys(@NotNull Item a, @NotNull Item b) {
            // Prefer the registry's natural ordering of the item keys.
            return Integer.compare(Item.getRawId(a), Item.getRawId(b));
        }

        @Override
        protected String getPath(@NotNull Item key) {
            final String id = Registries.ITEM.getId(key).getPath();

            return String.format("%s_%s", super.getRegistryPath(), id);
        }

        @Override
        public Set<Item> keys() {
            return this.items;
        }

    }

}
