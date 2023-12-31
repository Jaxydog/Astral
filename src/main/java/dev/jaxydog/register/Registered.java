package dev.jaxydog.register;

import dev.jaxydog.Astral;
import net.minecraft.util.Identifier;

/** Provides a set of interfaces for registering values at runtime. */
public interface Registered {

	/** Returns the value's associated registry identifier path. */
	String getIdPath();

	/** Returns the value's associated registry identifier. */
	default Identifier getId() {
		return Astral.getId(this.getIdPath());
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
