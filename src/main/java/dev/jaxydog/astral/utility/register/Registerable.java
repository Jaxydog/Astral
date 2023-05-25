package dev.jaxydog.astral.utility.register;

/**
 * Provides a set of interfaces to assist in value registration
 * ---
 * This interface does nothing by itself, but is extended by all contained interfaces
 * to allow methods to take any `Registerable` value, regardless of the target environment
 */
public interface Registerable extends Identifiable {
	/** A type that can be registered on the client environment */
	public static interface Client extends Registerable {
		/** Registers the value on the client environment */
		public default void registerClient() {}
	}

	/** A type that can be registered on the client environment */
	public static interface Main extends Registerable {
		/** Registers the value on the main environment */
		public default void registerMain() {}
	}

	/** A type that can be registered on the client environment */
	public static interface Server extends Registerable {
		/** Registers the value on the server environment */
		public default void registerServer() {}
	}
}
