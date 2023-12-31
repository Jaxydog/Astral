package dev.jaxydog.register;

import java.io.InvalidClassException;
import java.util.function.Consumer;

/** Used as a representation of the current environment during the automatic registration process. */
public enum RegistrationEnvironment {

	/** The client *and* the server. */
	COMMON(Registered.Common.class, r -> ((Registered.Common) r).register()),

	/** The client. */
	CLIENT(Registered.Client.class, r -> ((Registered.Client) r).registerClient()),

	/** The server. */
	SERVER(Registered.Server.class, r -> ((Registered.Server) r).registerServer()),

	/** The data generator. */
	DATA_GEN(Generated.class, r -> ((Generated) r).generate());

	/** The interface class. */
	private final Class<? extends Registered> registered;
	/** A method that consumes a value and registers it. */
	private final Consumer<? super Registered> consumer;

	RegistrationEnvironment(Class<? extends Registered> registered, Consumer<? super Registered> consumer) {
		this.registered = registered;
		this.consumer = consumer;
	}

	/** Returns this environment's associated interface. */
	public final Class<? extends Registered> getInterface() {
		return this.registered;
	}

	/** Registers the provided value within this environment. */
	public final void register(Registered registered) throws InvalidClassException {
		if (!this.getInterface().isInstance(registered)) {
			throw new InvalidClassException("Expected an instance of " + this.getInterface().getSimpleName());
		}

		this.consumer.accept(registered);
	}

	/** Returns whether the provided annotation is enabled in the current environment. */
	public final boolean shouldIgnore(IgnoreRegistration annotation) {
		return switch (this) {
			case COMMON -> annotation.common();
			case CLIENT -> annotation.client();
			case SERVER -> annotation.server();
			case DATA_GEN -> annotation.dataGen();
		};
	}

}
