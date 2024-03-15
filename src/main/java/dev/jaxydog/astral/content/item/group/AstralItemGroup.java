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
import dev.jaxydog.astral.register.Registered.Common;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * An extension of an {@link ItemGroup} that provides commonly used functionality.
 * <p>
 * This type is automatically registered.
 *
 * @author Jaxydog
 * @since 2.0.0
 */
public class AstralItemGroup extends ItemGroup implements Common {

    /** The item group's identifier path used within the registration system. */
    private final String path;

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
     * @param iconSupplier The item group's preferred icon.
     * @param entryCollector The item group's entry collector.
     *
     * @since 2.0.0
     */
    public AstralItemGroup(
        String path,
        Row row,
        int column,
        Type type,
        Text displayName,
        Supplier<ItemStack> iconSupplier,
        EntryCollector entryCollector
    ) {
        super(row, column, type, displayName, iconSupplier, entryCollector);

        this.path = path;
    }

    /**
     * Creates a new item group from a given source.
     * <p>
     * In most cases, you will want to instead use this class' builder.
     *
     * @param path The item group's identifier path.
     * @param group The source item group.
     * @param entryCollector The item group's entry collector.
     *
     * @since 2.0.0
     */
    protected AstralItemGroup(String path, ItemGroup group, EntryCollector entryCollector) {
        this(
            path,
            group.getRow(),
            group.getColumn(),
            group.getType(),
            group.getDisplayName(),
            group::getIcon,
            entryCollector
        );
    }

    /**
     * Returns a new builder for an {@link AstralItemGroup}.
     *
     * @param path The identifier path.
     *
     * @return A new builder.
     *
     * @since 2.0.0
     */
    @Contract("_ -> new")
    public static @NotNull Builder builder(String path) {
        return new Builder(path);
    }

    /**
     * Returns this item group's associated registry key.
     * <p>
     * If the item group has not been registered, this method will throw a run-time exception.
     *
     * @return The associated registry key.
     *
     * @since 2.0.0
     */
    public RegistryKey<ItemGroup> getRegistryKey() {
        return Registries.ITEM_GROUP.getKey(this).orElseThrow();
    }

    @Override
    public String getRegistryPath() {
        return this.path;
    }

    @Override
    public void registerCommon() {
        Registry.register(Registries.ITEM_GROUP, this.getRegistryId(), this);
    }

    /**
     * Builds and constructs an instance of a new {@link AstralItemGroup}.
     *
     * @author Jaxydog
     * @since 2.0.0
     */
    public static class Builder extends ItemGroup.Builder {

        /**
         * The item group's identifier path used within the registration system.
         *
         * @since 2.0.0
         */
        protected final String path;
        /**
         * The item group's inner entry collector.
         *
         * @since 2.0.0
         */
        protected EntryCollector entryCollector = (context, entries) -> {};
        /**
         * Whether this item group builder has set a display name.
         *
         * @since 2.0.0
         */
        protected boolean hasDisplayName = false;

        /**
         * Creates a new builder instance.
         * <p>
         * This is only accessible through {@link #builder(String)} or subclasses.
         *
         * @param path The identifier path.
         *
         * @since 2.0.0
         */
        protected Builder(String path) {
            // Mimic's the default Fabric builder's constructor.
            super(null, -1);

            this.path = path;
        }

        @Override
        public Builder displayName(Text displayName) {
            this.hasDisplayName = true;

            return (Builder) super.displayName(displayName);
        }

        @Override
        public Builder entries(EntryCollector entryCollector) {
            this.entryCollector = entryCollector;

            return this;
        }

        @Override
        public Builder icon(Supplier<ItemStack> iconSupplier) {
            return (Builder) super.icon(iconSupplier);
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

        /**
         * Builds the item group.
         * <p>
         * This should be used over {@link Builder#build}.
         * <p>
         * This is not an override because attempting to override build causes a recursion-induced stack overflow.
         *
         * @return The item group.
         *
         * @since 2.0.0
         */
        public AstralItemGroup buildGroup() {
            final ItemGroup group;

            // Only set a name if one has not yet been set.
            if (!this.hasDisplayName) {
                final Text name = Text.translatable(Astral.getId(this.path).toTranslationKey("itemGroup"));

                group = super.displayName(name).build();
            } else {
                group = super.build();
            }

            return new AstralItemGroup(this.path, group, this.entryCollector);
        }

    }

}
