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

package dev.jaxydog.astral.content.loader.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.loader.AstralDataLoader;
import dev.jaxydog.astral.utility.CurrencyUtil.ItemRepresenting;
import dev.jaxydog.astral.utility.CurrencyUtil.Reward;
import dev.jaxydog.astral.utility.CurrencyUtil.Skeleton;
import dev.jaxydog.astral.utility.CurrencyUtil.Unit;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Loads JSON data for Astral's currency system.
 *
 * @author Jaxydog
 */
public class CurrencyLoader extends AstralDataLoader {

    public CurrencyLoader(String dataType) {
        super(dataType);
    }

    /**
     * Parses and loads currency data generically.
     *
     * @param object The JSON source object.
     * @param parse Parses and constructs a new instance of the generic type.
     * @param loader Loads a completed listing of parsed types.
     * @param descriptor Describes the type as a string, used in logging.
     * @param <T> The type to be loaded.
     */
    protected <T extends ItemRepresenting> void load(
        JsonObject object,
        BiFunction<Identifier, JsonObject, T> parse,
        Function<Map<Identifier, T>, Integer> loader,
        String descriptor
    ) {
        final Map<Identifier, T> output = new Object2ObjectArrayMap<>(object.size());

        object.asMap().forEach((key, value) -> {
            final Identifier identifier, itemIdentifier;

            if ((identifier = Identifier.tryParse(key)) == null) {
                Astral.LOGGER.warn("Invalid identifier key '{}'", key);

                return;
            }
            if (output.containsKey(identifier)) {
                Astral.LOGGER.warn("Duplicate identifier key '{}'", key);

                return;
            }
            if (!(value instanceof final JsonObject valueObject)) {
                Astral.LOGGER.warn("Expected an object for key '{}'", key);

                return;
            }

            try {
                if ((itemIdentifier = Identifier.tryParse(JsonHelper.getString(valueObject, "item"))) == null) {
                    Astral.LOGGER.warn("Invalid item identifier '{}'", key);

                    return;
                }

                output.put(identifier, parse.apply(itemIdentifier, valueObject));
            } catch (JsonParseException exception) {
                Astral.LOGGER.warn(exception.getLocalizedMessage());
            }
        });

        Astral.LOGGER.info("Loaded {} currency {}", loader.apply(output), descriptor);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        prepared.entrySet().stream().filter(e -> e.getValue() instanceof JsonObject).forEach(entry -> {
            final JsonObject object = entry.getValue().getAsJsonObject();
            final String path = entry.getKey().getPath();

            switch (path.replaceFirst("\\.json$", "")) {
                case "units" -> this.load(object, Unit::parse, Unit.UNITS::load, "units");
                case "rewards" -> this.load(object, Reward::parse, Reward.REWARDS::load, "rewards");
                case "skeletons" -> this.load(object, Skeleton::parse, Skeleton.SKELETONS::load, "skeletons");
            }
        });
    }

}
