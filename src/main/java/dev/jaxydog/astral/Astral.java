/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023–2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral;

import dev.jaxydog.astral.content.CustomContent;
import dev.jaxydog.astral.utility.CurrencyUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The mod's common entrypoint.
 * <p>
 * This class is initialized on both the client and the server environments. All code referenced here must be able to be
 * shared between environments, or else it may cause runtime crashes. Please use with a healthy dose of caution!
 * <p>
 * It should also be assumed that prior to {@link #onInitialize()}, nothing has been fully loaded. For safety purposes,
 * you may use the {@link #hasInitialized()} method to verify whether this is the case.
 *
 * @author Jaxydog
 * @see AstralClient
 * @see AstralServer
 * @see AstralDataGenerator
 */
public final class Astral implements ModInitializer {

    /**
     * The mod's identifier.
     * <p>
     * This is most commonly used for generating {@link Identifier}s.
     */
    public static final String MOD_ID = "astral";
    /**
     * The mod's logger instance.
     * <p>
     * This is used extensively throughout the codebase to interface with Minecraft's console.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Tracks whether the mod has been initialized.
     * <p>
     * This will be true only if an instance of the common initializer has fully loaded.
     */
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    /**
     * Returns a new {@link Identifier} using the mod's identifier namespace ({@value #MOD_ID}) and the provided path.
     *
     * @param path The identifier's path.
     *
     * @return A new identifier.
     */
    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }

    /**
     * Returns whether the mod has finished initializing.
     * <p>
     * This method will always return {@code false} during the registration process.
     * <p>
     * If an instance of {@link Astral} has been initialized, this will return {@code true}.
     *
     * @return If the mod is initialized.
     */
    public static boolean hasInitialized() {
        return INITIALIZED.get();
    }

    /**
     * Returns the mod metadata for Astral.
     * <p>
     * If this is called before Fabric itself has loaded it will cause the game to crash, however in normal runtime
     * conditions this is impossible.
     *
     * @return The mod's metadata.
     */
    public static Optional<ModMetadata> getMetadata() {
        return FabricLoader.getInstance().getModContainer(MOD_ID).map(ModContainer::getMetadata);
    }

    @Override
    public void onInitialize() {
        if (hasInitialized()) throw new IllegalStateException("The mod may not be initialized more than once");

        CustomContent.INSTANCE.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CurrencyUtil.Loader());

        getMetadata().ifPresent(metadata -> {
            final String displayName = metadata.getName();
            final String version = metadata.getVersion().getFriendlyString();

            LOGGER.info("{} v{} has loaded! Thank you for playing with us <3", displayName, version);
        });

        INITIALIZED.set(true);
    }

}
