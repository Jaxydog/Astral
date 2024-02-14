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

package dev.jaxydog.astral.content.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An extension of an {@link PotionItem} that provides commonly used functionality.
 * <p>
 * This is one of the various instances of already-provided wrapper classes for commonly used types.
 * <p>
 * In future code, you should prefer to extend this class over {@link PotionItem} if at all possible.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 * @see Custom
 */
public class AstralPotionItem extends PotionItem implements Custom, LoreHolder {

    /** The item's identifier path used within the registration system. */
    private final String path;
    /** The item's preferred item group, or {@code null} if it should not be added to any group. */
    private final @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup;

    /**
     * Creates a new potion item using the given settings.
     * <p>
     * If the {@link #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     */
    public AstralPotionItem(String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup) {
        super(settings);

        this.path = path;
        this.preferredGroup = preferredGroup;
    }

    /**
     * Creates a new potion item using the given settings.
     * <p>
     * This item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     */
    public AstralPotionItem(String path, Settings settings) {
        this(path, settings, null);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(this.getLoreTooltips(stack));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Optional<RegistryKey<ItemGroup>> getItemGroup() {
        return Optional.ofNullable(this.preferredGroup).map(Supplier::get);
    }

    @Override
    public String getRegistryPath() {
        return this.path;
    }

}
