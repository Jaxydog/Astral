package dev.jaxydog.utility.register;

import dev.jaxydog.Astral;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

/** Provides a set of interfaces for registering values at runtime */
public interface Registerable {

	/** Returns the value's associated identifier */
	default Identifier getId() {
		return Astral.getId(this.getRawId());
	}

	/** Returns the value's *raw* identifier, used for generating the actual identifier */
	String getRawId();

	/** Used in the registration process to distinguish between different target environments */
	enum Env {

		/** The client and the server */
		MAIN(Main.class, "registerMain"),
		/** The client */
		CLIENT(Client.class, "registerClient"),
		/** The server */
		SERVER(Server.class, "registerServer"),
		/** The data generation client */
		DATA_GEN(DataGen.class, "registerDataGen");

		/** The environment's corresponding registerable interface */
		private final Class<? extends Registerable> INTERFACE;
		/** The name of the method used to register the value */
		private final String METHOD_NAME;

		Env(Class<? extends Registerable> iface, String methodName) {
			this.INTERFACE = iface;
			this.METHOD_NAME = methodName;
		}

		/** Returns the environment's registration method */
		public final Method getMethod() throws NoSuchMethodException {
			if (this == Env.DATA_GEN) {
				return this.getInterface().getMethod(this.METHOD_NAME, Pack.class);
			} else {
				return this.getInterface().getMethod(this.METHOD_NAME);
			}
		}

		/** Returns the environment's corresponding registerable interface */
		public final Class<? extends Registerable> getInterface() {
			return this.INTERFACE;
		}

		/** Returns whether a field should be skipped based on its annotation */
		public final boolean isSkipped(Skip annotation) {
			return switch (this) {
				case MAIN -> annotation.main();
				case CLIENT -> annotation.client();
				case SERVER -> annotation.server();
				case DATA_GEN -> annotation.dataGen();
			};
		}

	}

	/** A value registered on both the client and the server */
	interface Main extends Registerable {

		/** Registers the value on both the client and server */
		void registerMain();

	}

	/** A value registered on the client */
	interface Client extends Registerable {

		/** Registers the value on the client */
		void registerClient();

	}

	/** A value registered on the server */
	interface Server extends Registerable {

		/** Registers the value on the server */
		void registerServer();

	}

	/** A value registered on the data generation client */
	interface DataGen extends Registerable {

		/** Registers the value on the data generation client */
		void registerDataGen(Pack generator);

	}

	/** A value registered in all environments */
	interface All extends Main, Client, Server, DataGen {}

}
