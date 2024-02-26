package dev.jaxydog.astral.register;

import dev.jaxydog.astral.Astral;
import net.minecraft.util.Identifier;

/** Provides a set of interfaces for registering values at runtime. */
public interface Registered {

	/** Returns the value's associated registry identifier path. */
	String getRegistryIdPath();

	/** Returns the value's associated registry identifier. */
	default Identifier getRegistryId() {
		return Astral.getId(this.getRegistryIdPath());
	}

	/** A value registered on the client *and* the server. */
	interface Common extends Registered {

		/** Registers the content on the client *and* the server. */
		void register();

	}

	/** A value registered on the client. */
	interface Client extends Registered {

		/** Registers the content on the client. */
		void registerClient();

	}

	/** A value registered on the server. */
	interface Server extends Registered {

		/** Registers the content on the server. */
		void registerServer();

	}

	/** A value registered in all environments. */
	interface All extends Common, Client, Server {

	}

}
