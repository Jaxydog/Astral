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

import dev.jaxydog.astral.content.AstralContent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The mod's common entrypoint.
 * <p>
 * This class is initialized on both the client and the server. All code referenced here must be able to be shared
 * between environments, or else it may cause runtime crashes. Please use with a healthy dose of caution!
 * <p>
 * It should also be assumed that prior to {@link #onInitialize()}, nothing has been loaded. For safety purposes, you
 * may use the {@link #hasInitialized()} method to verify whether this is the case.
 *
 * @author Jaxydog
 */
public final class Astral implements ModInitializer {

    /**
     * The mod's identifier.
     * <p>
     * This is most commonly used for generating {@link net.minecraft.util.Identifier}s.
     */
    private static final String MOD_ID = "astral";
    /**
     * The mod's logger instance.
     * <p>
     * This is used extensively throughout the codebase to interface with Minecraft's console.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Astral.class.getSimpleName());

    /**
     * Tracks whether the mod has been initialized.
     * <p>
     * This will be true if an instance of the common initializer has loaded.
     */
    private static boolean hasInitialized = false;

    /**
     * Returns the mod's initialized logger instance.
     *
     * @return The logger instance.
     */
    @Contract(pure = true)
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns the mod's identifier namespace.
     * <p>
     * This is most commonly used for generating {@link net.minecraft.util.Identifier}s.
     *
     * @return The mod's namespace.
     */
    @Contract(pure = true)
    public static String getModId() {
        return MOD_ID;
    }

    /**
     * Returns a new {@link Identifier} using the mod's identifier namespace and the provided path.
     *
     * @param path The identifier's path.
     *
     * @return A new identifier.
     */
    public static Identifier getModId(String path) {
        return Identifier.of(getModId(), path);
    }

    /**
     * Returns the mod metadata for Astral.
     * <p>
     * If this is called before Fabric itself has loaded it will cause the game to crash, however in normal runtime
     * conditions this is impossible.
     *
     * @return The mod's metadata.
     */
    public static Optional<ModMetadata> getModMetadata() {
        return FabricLoader.getInstance().getModContainer(getModId()).map(ModContainer::getMetadata);
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
    @Contract(pure = true)
    public static boolean hasInitialized() {
        return hasInitialized;
    }

    @Override
    public void onInitialize() throws IllegalStateException {
        if (hasInitialized()) {
            throw new IllegalStateException("The mod may not be initialized more than once");
        }

        AstralContent.INSTANCE.register();

        getModMetadata().ifPresent(metadata -> {
            final String displayName = metadata.getName();
            final String version = metadata.getVersion().getFriendlyString();

            getLogger().info("{} v{} has loaded! Thank you for playing with us <3", displayName, version);
        });

        hasInitialized = true;
    }

}
