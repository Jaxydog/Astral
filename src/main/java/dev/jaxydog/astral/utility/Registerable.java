package dev.jaxydog.astral.utility;

/**
 * Provides interfaces to assist in value registration
 * ---
 * This does nothing by itself, but it is extended by all contained interfaces to allow
 * methods to take in any `Registerable` just in case it's given values or methods in the future
 */
public interface Registerable extends Identifiable {
	/** A value which can be registered in the main mod class */
	public static interface Main extends Registerable {
		/** Registers the value in the main environment */
		public default void registerMain() {}
	}

	/** A value which can be registered in the client mod class */
	public static interface Client extends Registerable {
		/** Registers the value in the client environment */
		public default void registerClient() {}
	}

	/** A value which can be registered in the server mod class */
	public static interface Server extends Registerable {
		/** Registers the value in the server environment */
		public default void registerServer() {}
	}
}
