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

package dev.jaxydog.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.utility.AstralModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(Model.class)
public class ModelMixin implements AstralModel {

    @Unique
    private final Map<String, JsonElement> customJson = new Object2ObjectOpenHashMap<>();

    @Shadow
    @Final
    private Optional<Identifier> parent;

    @Shadow
    @Final
    private Optional<String> variant;

    @Shadow
    @Final
    private Set<TextureKey> requiredTextures;

    @Inject(method = "createJson", at = @At(value = "RETURN", shift = Shift.BEFORE))
    private void injectCustomJson(
        Identifier id,
        Map<TextureKey, Identifier> textures,
        CallbackInfoReturnable<JsonObject> callbackInfo,
        @Local(ordinal = 0) JsonObject jsonObject
    ) {
        this.customJson.forEach(jsonObject::add);
    }

    @Override
    public void astral$addCustomJson(String key, JsonElement element) {
        this.customJson.put(key, element);
    }

    @Override
    public void astral$addCustomJson(Map<String, JsonElement> map) {
        this.customJson.putAll(map);
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public Model astral$copy() {
        final TextureKey[] textures = this.requiredTextures.toArray(new TextureKey[0]);
        final Model model = new Model(this.parent, this.variant, textures);

        ((AstralModel) model).astral$addCustomJson(this.customJson);

        return model;
    }

}
