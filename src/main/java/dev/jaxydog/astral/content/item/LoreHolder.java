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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Provides common functionality for items that may contain lore tooltips.
 * <p>
 * By itself, this interface does not handle registration. It should usually be used in tandem with the {@link Custom}
 * interface, which handles common registration as well.
 *
 * @author Jaxydog
 */
public interface LoreHolder {

    /**
     * Formats the given mutable text instance to align with this item's preferred lore formatting.
     * <p>
     * The default implementation simply makes the text gray. Any and all prior formatting will be preserved.
     *
     * @param lore The mutable text.
     *
     * @return A reference to the formatted text.
     */
    default Text formatLoreText(MutableText lore) {
        return lore.formatted(Formatting.GRAY);
    }

    /**
     * Returns the lore tooltips assigned to the given item stack.
     * <p>
     * By default, this method returns a list of tooltips assigned via the active language file. Keys are determined
     * from {@link ItemStack#getTooltip}, with a {@code `.lore_`} suffix appended containing an integer identifier.
     * <p>
     * This identifier will start at {@code 0}, incrementing by one until the language file no longer contains the
     * computed key.
     *
     * @param stack The target item stack.
     *
     * @return A list of assigned tooltips.
     */
    default List<Text> getLoreTooltips(ItemStack stack) {
        final String key = stack.getTranslationKey() + ".lore_";
        final List<Text> tooltips = new ObjectArrayList<>();

        for (int iteration = 0; I18n.hasTranslation(key + iteration); iteration += 1) {
            final MutableText text = Text.translatable(key + iteration);

            tooltips.add(this.formatLoreText(text));
        }

        return tooltips;
    }

}
