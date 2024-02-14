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

package dev.jaxydog.astral.content.item.group;

import dev.jaxydog.astral.Astral;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * An extension of an {@link AstralItemGroup} that provides a cycling icon.
 * <p>
 * If you do not need more than one icon, it's recommended to use {@link AstralItemGroup} instead.
 *
 * @author Jaxydog
 */
public class CyclingItemGroup extends AstralItemGroup {

    /** A list containing all valid icons for this item group. */
    protected final List<Supplier<ItemStack>> iconSuppliers = new ObjectArrayList<>();
    /** A duration in ticks that determines the cycle speed of the icons. */
    protected final int cycleInterval;

    /** An index into the supplier list that determines the active icon. */
    private int iconIndex = 0;
    /** A counter that controls when to increment and cycle the active icon. */
    private float cycleProgress = 0F;

    /**
     * Creates a new item group.
     * <p>
     * In most cases, you will want to instead use this class' builder.
     *
     * @param path The item group's identifier path.
     * @param row The item group's row.
     * @param column The item group's column.
     * @param type The item group's type.
     * @param displayName The item group's display name.
     * @param entryCollector The item group's entry collector.
     * @param icons The item group's preferred icons.
     * @param cycleInterval The duration in ticks that determines the length of stay for one icon.
     */
    @SuppressWarnings("unused")
    public CyclingItemGroup(
        String path,
        Row row,
        int column,
        Type type,
        Text displayName,
        EntryCollector entryCollector,
        List<Supplier<ItemStack>> icons,
        int cycleInterval
    ) {
        super(path, row, column, type, displayName, () -> ItemStack.EMPTY, entryCollector);

        this.iconSuppliers.addAll(icons);
        this.cycleInterval = cycleInterval;
    }

    /**
     * Creates a new item group from a given source.
     * <p>
     * In most cases, you will want to instead use this class' builder.
     *
     * @param path The item group's identifier path.
     * @param group The source item group.
     * @param entryCollector The item group's entry collector.
     * @param icons The item group's preferred icons.
     * @param cycleInterval The duration in ticks that determines the length of stay for one icon.
     */
    protected CyclingItemGroup(
        String path, ItemGroup group, EntryCollector entryCollector, List<Supplier<ItemStack>> icons, int cycleInterval
    ) {
        super(path, group, entryCollector);

        this.iconSuppliers.addAll(icons);
        this.cycleInterval = cycleInterval;
    }

    /**
     * Returns a new builder for a {@link CyclingItemGroup}.
     *
     * @param path The identifier path.
     *
     * @return A new builder.
     */
    @Contract("_ -> new")
    public static @NotNull Builder builder(String path) {
        return new Builder(path);
    }

    @Override
    public ItemStack astral$getIcon(float delta) {
        this.cycleProgress += delta;

        // If the progress exceeds the interval, wrap the progress and update the index.
        if (this.cycleProgress >= (float) this.cycleInterval) {
            this.cycleProgress %= (float) this.cycleInterval;

            // Only update & wrap the index if there are at least two icons.
            if (this.iconSuppliers.size() > 1) {
                this.iconIndex += 1;
                this.iconIndex %= this.iconSuppliers.size();
            }
        }

        // Only return an icon if the current index is within bounds.
        if (this.iconSuppliers.size() > this.iconIndex) {
            return this.iconSuppliers.get(this.iconIndex).get();
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Builds and constructs an instance of a new {@link AstralItemGroup}.
     *
     * @author Jaxydog
     */
    public static class Builder extends AstralItemGroup.Builder {

        /** A list of the currently added icon suppliers. */
        // It's safe to assume that at least two icons will be added.
        protected final List<Supplier<ItemStack>> iconSuppliers = new ObjectArrayList<>(2);
        /** A duration in ticks that determines the cycle speed of the icons. */
        protected int cycleInterval = 40;

        /**
         * Creates a new builder instance.
         * <p>
         * This is only accessible through {@link #builder(String)} or subclasses.
         *
         * @param path The identifier path.
         */
        protected Builder(String path) {
            super(path);
        }

        /**
         * Sets the duration in ticks that determines the cycle speed of the icons.
         *
         * @param ticks The duration in ticks.
         *
         * @return The builder instance.
         */
        public Builder cycleInterval(int ticks) {
            this.cycleInterval = ticks;

            return this;
        }

        @Override
        public Builder displayName(Text displayName) {
            return (Builder) super.displayName(displayName);
        }

        @Override
        public Builder entries(EntryCollector entryCollector) {
            return (Builder) super.entries(entryCollector);
        }

        @Override
        public Builder icon(Supplier<ItemStack> iconSupplier) {
            this.iconSuppliers.add(iconSupplier);

            return this;
        }

        @Override
        public Builder noRenderedName() {
            return (Builder) super.noRenderedName();
        }

        @Override
        public Builder noScrollbar() {
            return (Builder) super.noScrollbar();
        }

        @Override
        public Builder special() {
            return (Builder) super.special();
        }

        @Override
        public Builder texture(String texture) {
            return (Builder) super.texture(texture);
        }

        @Override
        protected Builder type(Type type) {
            return (Builder) super.type(type);
        }

        @Override
        public CyclingItemGroup build() {
            final ItemGroup group;

            // Only set a name if one has not yet been set.
            if (!this.hasDisplayName) {
                final Text name = Text.translatable(Astral.getModId(this.path).toTranslationKey("itemGroup"));

                group = super.displayName(name).build();
            } else {
                group = super.build();
            }

            return new CyclingItemGroup(this.path, group, this.entryCollector, this.iconSuppliers, this.cycleInterval);
        }

    }

}
