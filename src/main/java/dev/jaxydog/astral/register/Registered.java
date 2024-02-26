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

package dev.jaxydog.astral.register;

import dev.jaxydog.astral.Astral;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

/**
 * Provides a set of interfaces that enable the automatic registration system.
 * <p>
 * Usually, you do not want to implement {@link Registered} directly. In most cases you will want one of its provided
 * sub-interfaces.
 *
 * @see Common
 * @see Client
 * @see Server
 * @see Generated
 */
public interface Registered {

    /**
     * Returns this value's associated registry identifier path.
     * <p>
     * This is by default used to determine the {@link Identifier} provided by {@link #getRegistryId()}. For example,
     * returning {@code "my_item"} will cause the associated identifier to be {@code astral:my_item}.
     *
     * @return An identifier path.
     */
    String getRegistryPath();

    /**
     * Returns this value's associated registry identifier.
     * <p>
     * This is used to uniquely distinguish this instance from other equally-typed instances.
     *
     * @return A new identifier.
     */
    default Identifier getRegistryId() {
        return Astral.getId(this.getRegistryPath());
    }

    /**
     * Represents a type that should be registered on both the client and server environments.
     * <p>
     * Note that this is distinctly different from {@link Client} and {@link Server}.
     *
     * @author Jaxydog
     * @see Client
     * @see Server
     * @see Generated
     */
    interface Common extends Registered {

        /**
         * Registers this value on both the client and server environments.
         * <p>
         * The contained code will run for both environments. If you only want one or the other, see its sister
         * interfaces.
         *
         * @see Client
         * @see Server
         * @see Generated
         */
        void register();

    }

    /**
     * Represents a type that should be registered on the client, but not the server environment.
     *
     * @author Jaxydog
     * @see Common
     * @see Server
     * @see Generated
     */
    @Environment(EnvType.CLIENT)
    interface Client extends Registered {

        /**
         * Registers this value on the client environment.
         * <p>
         * The contained code will only ever run on the game client. If you only want the server or want both, see its
         * sister interfaces.
         *
         * @see Common
         * @see Server
         * @see Generated
         */
        void registerClient();

    }

    /**
     * Represents a type that should be registered on the server, but not the client environment.
     *
     * @author Jaxydog
     * @see Common
     * @see Client
     * @see Generated
     */
    @Environment(EnvType.SERVER)
    interface Server extends Registered {

        /**
         * Registers this value on the server environment.
         * <p>
         * The contained code will only ever run on the game server. If you only want the client or want both, see its
         * sister interfaces.
         *
         * @see Common
         * @see Client
         * @see Generated
         */
        void registerServer();

    }

    /**
     * Represents a type that generates assets or data.
     *
     * @author Jaxydog
     * @see Common
     * @see Client
     * @see Server
     */
    interface Generated extends Registered {

        /**
         * Generates game assets or data.
         * <p>
         * It should be assumed that the contained code will only ever run on the server environment.
         */
        void generate();

    }

    /**
     * Represents a type that is registered in all possible mod environments.
     * <p>
     * Notably, this interface does not extend {@link Generated}. This is because {@link Generated} is only ever used
     * during development, and is therefore not helpful under normal circumstances.
     * <p>
     * In most cases this is overkill, and one of its extended interfaces should be used instead.
     *
     * @author Jaxydog
     * @see Common
     * @see Client
     * @see Server
     * @see Generated
     */
    interface All extends Common, Client, Server {

    }

}
