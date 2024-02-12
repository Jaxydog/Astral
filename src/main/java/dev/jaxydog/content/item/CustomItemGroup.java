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

package dev.jaxydog.content.item;

import dev.jaxydog.Astral;
import dev.jaxydog.register.Registered;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * Simple extension to allow automatic registration of item groups.
 *
 * @author Jaxydog
 */
public class CustomItemGroup extends ItemGroup implements Registered.Common {

    private final String idPath;

    private CustomItemGroup(
        String idPath,
        Row row,
        int column,
        Type type,
        Text displayName,
        Supplier<ItemStack> iconSupplier,
        EntryCollector entryCollector
    ) {
        super(row, column, type, displayName, iconSupplier, entryCollector);

        this.idPath = idPath;
    }

    private CustomItemGroup(String idPath, ItemGroup group, EntryCollector entryCollector) {
        this(
            idPath,
            group.getRow(),
            group.getColumn(),
            group.getType(),
            group.getDisplayName(),
            group::getIcon,
            entryCollector
        );
    }

    public static Builder builder(String idPath) {
        return new Builder(idPath);
    }

    public RegistryKey<ItemGroup> getRegistryKey() {
        return Registries.ITEM_GROUP.getKey(this).orElseThrow();
    }

    @Override
    public String getRegistryIdPath() {
        return this.idPath;
    }

    @Override
    public void register() {
        Registry.register(Registries.ITEM_GROUP, this.getRegistryId(), this);
    }

    public static final class Builder extends ItemGroup.Builder {

        private final String idPath;
        private EntryCollector entryCollector = (a, b) -> { };

        public Builder(String idPath) {
            super(null, -1);

            this.idPath = idPath;
        }

        @Override
        public Builder icon(Supplier<ItemStack> iconSupplier) {
            return (Builder) super.icon(iconSupplier);
        }

        @Override
        public Builder entries(EntryCollector entryCollector) {
            this.entryCollector = entryCollector;

            return this;
        }

        @Override
        public Builder special() {
            return (Builder) super.special();
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
        protected Builder type(Type type) {
            return (Builder) super.type(type);
        }

        @Override
        public Builder texture(String texture) {
            return (Builder) super.texture(texture);
        }

        public CustomItemGroup finish() {
            final Text name = Text.translatable(Astral.getId(this.idPath).toTranslationKey("itemGroup"));

            return new CustomItemGroup(this.idPath, super.displayName(name).build(), this.entryCollector);
        }

    }

}
