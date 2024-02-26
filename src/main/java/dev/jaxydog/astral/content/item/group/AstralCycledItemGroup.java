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

import java.util.List;
import java.util.function.Supplier;

/**
 * An extension of a {@link AstralItemGroup} that provides a cycling item icon.
 *
 * @author Jaxydog
 */
public class AstralCycledItemGroup extends AstralItemGroup {

    private final List<Supplier<ItemStack>> stacks;
    private final int interval;

    private int index = 0;
    private float progress = 0F;

    private AstralCycledItemGroup(
        String idPath,
        Row row,
        int column,
        Type type,
        Text displayName,
        EntryCollector entryCollector,
        List<Supplier<ItemStack>> stacks,
        int interval
    ) {
        super(idPath, row, column, type, displayName, () -> ItemStack.EMPTY, entryCollector);

        this.stacks = stacks;
        this.interval = interval;
    }

    protected AstralCycledItemGroup(
        String idPath, ItemGroup group, EntryCollector entryCollector, List<Supplier<ItemStack>> stacks, int interval
    ) {
        this(idPath,
            group.getRow(),
            group.getColumn(),
            group.getType(),
            group.getDisplayName(),
            entryCollector,
            stacks,
            interval
        );
    }

    public static Builder builder(String idPath) {
        return new Builder(idPath);
    }

    @Override
    public ItemStack astral$getIcon(float delta) {
        this.progress += delta;

        if (this.progress >= this.interval) {
            this.progress %= this.interval;

            if (!this.stacks.isEmpty()) {
                this.index += 1;
                this.index %= this.stacks.size();
            }
        }

        if (this.stacks.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            return this.stacks.get(this.index).get();
        }
    }

    public static class Builder extends AstralItemGroup.Builder {

        private final List<Supplier<ItemStack>> stacks = new ObjectArrayList<>(1);
        private int interval = 40;

        public Builder(String idPath) {
            super(idPath);
        }

        @Override
        public Builder icon(Supplier<ItemStack> iconSupplier) {
            this.stacks.add(iconSupplier);
            return this;
        }

        public Builder interval(int ticks) {
            this.interval = ticks;
            return this;
        }

        @Override
        public Builder entries(EntryCollector entryCollector) {
            return (Builder) super.entries(entryCollector);
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

        @Override
        public AstralCycledItemGroup finish() {
            final Text name = Text.translatable(Astral.getId(this.idPath).toTranslationKey("itemGroup"));

            return new AstralCycledItemGroup(this.idPath,
                super.displayName(name).build(),
                this.entryCollector,
                this.stacks,
                this.interval
            );
        }

    }

}
