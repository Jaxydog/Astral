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

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.item.AstralItem;
import dev.jaxydog.astral.register.Registered.Client;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Implements a simple breakable mirror item.
 * <p>
 * By default, unless overridden, all stacks start out as being unbroken.
 * <p>
 * A mirror item's state can be controlled via the {@value #BROKEN_KEY} boolean NBT key.
 *
 * @author Jaxydog
 */
@SuppressWarnings("unused")
public class MirrorItem extends AstralItem implements Client {

    /**
     * The NBT key that determines whether a mirror item is considered broken.
     * <p>
     * The value assigned to this is expected to be boolean-coercible.
     */
    public static final String BROKEN_KEY = "Broken";

    /**
     * The identifier corresponding to the mirror item's broken model.
     * <p>
     * This is expected to be used solely within the {@link ModelPredicateProviderRegistry}.
     */
    public static final Identifier BROKEN_MODEL_ID = Astral.getId("broken");

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     */
    public MirrorItem(String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup) {
        super(path, settings, preferredGroup);
    }

    /**
     * Creates a new mirror item using the given settings.
     * <p>
     * This item will be added to the default item group.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     */
    public MirrorItem(String path, Settings settings) {
        super(path, settings);
    }

    /**
     * Returns whether the given item stack is considered broken.
     * <p>
     * If the NBT key is missing, or present but not boolean-coercible, {@code false} is always returned.
     *
     * @param stack The item stack.
     *
     * @return If the mirror is broken.
     */
    public boolean isBroken(ItemStack stack) {
        final NbtCompound compound = stack.getNbt();

        // If the value is not set, this will always return false.
        if (compound == null || !compound.contains(BROKEN_KEY)) {
            return false;
        } else {
            return compound.getBoolean(BROKEN_KEY);
        }
    }

    /**
     * Sets whether the given item stack is considered broken.
     *
     * @param stack The item stack.
     * @param broken Whether the mirror is broken.
     */
    public void setBroken(ItemStack stack, boolean broken) {
        stack.getOrCreateNbt().putBoolean(BROKEN_KEY, broken);
    }

    /**
     * Returns the data used in the mirror's item model that determines the texture to use.
     * <p>
     * If the stack represents a broken mirror, this method should return {@code 1F}. Otherwise, if the stack represents
     * a non-broken mirror, this method should return {@code 0F} (or any other value expected by its model).
     * <p>
     * The default behavior is to always return either {@code 1F} or {@code 0F}.
     *
     * @param stack The item stack.
     * @param world The world instance.
     * @param entity The entity holding the mirror.
     * @param seed The randomness seed.
     *
     * @return The data used to determine the mirror's texture.
     */
    protected float getBrokenModelData(ItemStack stack, World world, LivingEntity entity, int seed) {
        return this.isBroken(stack) ? 1F : 0F;
    }

    @Override
    public ItemStack getDefaultStack() {
        final ItemStack stack = super.getDefaultStack();

        this.setBroken(stack, false);

        return stack;
    }

    @Override
    public void registerClient() {
        // Tells the game to render a different model depending on the value provided by `getBrokenModelData`.
        ModelPredicateProviderRegistry.register(this, BROKEN_MODEL_ID, this::getBrokenModelData);
    }

}
