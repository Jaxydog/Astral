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

import dev.jaxydog.astral.register.Registered;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Provides common functionality to custom item wrappers.
 *
 * @author Jaxydog
 */
public interface Custom extends ItemConvertible, Registered.Common {

    /**
     * Returns this item's preferred item group.
     *
     * @return A reference to an item group.
     */
    default RegistryKey<ItemGroup> getItemGroup() {
        return CustomItemGroups.DEFAULT.getRegistryKey();
    }

    /**
     * Returns a list of lore tooltips specified within the active lang file.
     *
     * @param stack The item stack.
     *
     * @return A list of tooltips.
     */
    default List<Text> getLoreTooltips(ItemStack stack) {
        final String key = stack.getTranslationKey() + ".lore_";
        final List<Text> lore = new ObjectArrayList<>();

        for (int index = 0; I18n.hasTranslation(key + index); index += 1) {
            final Text text = Text.translatable(key + index).formatted(Formatting.GRAY);

            lore.add(text);
        }

        return lore;
    }

    @Override
    default void register() {
        Registry.register(Registries.ITEM, this.getRegistryId(), this.asItem());
        ItemGroupEvents.modifyEntriesEvent(this.getItemGroup()).register(g -> g.add(this.asItem().getDefaultStack()));
    }

}
