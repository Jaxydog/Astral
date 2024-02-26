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

package dev.jaxydog.astral.content.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jaxydog.astral.register.Registered.Common;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public abstract class AstralDataLoader extends JsonDataLoader implements Common, IdentifiableResourceReloadListener {

    /** The preferred GSON configuration of Astral's data loaders. */
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    /** The data type of this loader; this determines the expected directory within the `data` folder. */
    protected final String dataType;

    public AstralDataLoader(String dataType) {
        super(GSON, dataType);

        this.dataType = dataType;
    }

    @Override
    public Identifier getFabricId() {
        return this.getRegistryId();
    }

    @Override
    public String getRegistryPath() {
        return this.dataType;
    }

    @Override
    public void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
    }

}
