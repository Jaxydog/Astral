package dev.jaxydog.astral.utility.register;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.CustomGamerules;
import dev.jaxydog.astral.content.block.CustomBlocks;
import dev.jaxydog.astral.content.data.CustomData;
import dev.jaxydog.astral.content.effect.CustomStatusEffects;
import dev.jaxydog.astral.content.enchantment.CustomEnchantments;
import dev.jaxydog.astral.content.item.CustomItems;
import dev.jaxydog.astral.content.power.CustomActions;
import dev.jaxydog.astral.content.power.CustomConditions;
import dev.jaxydog.astral.content.power.CustomPowers;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Provides an implementation for mod content auto-registration */
public final class AutoRegisterImpl {

	/** The classes that should be automatically checked for registerable values */
	public static final Class<?>[] DEFINITIONS_CLASSES = new Class<?>[] {
		CustomActions.class,
		CustomBlocks.class,
		CustomConditions.class,
		CustomData.class,
		CustomEnchantments.class,
		CustomGamerules.class,
		CustomItems.class,
		CustomPowers.class,
		CustomStatusEffects.class,
	};

	private AutoRegisterImpl() {}

	/** Attempts to automatically register all values within the given definition class in the specified environment */
	private static <R extends Registerable> void run(RegisterEnv env, Class<?> definitions) {
		if (!definitions.isAnnotationPresent(AutoRegister.class)) return;
		if (!env.shouldRegister(definitions.getAnnotation(AutoRegister.class))) return;

		for (var field : definitions.getFields()) {
			if (field.isAnnotationPresent(SkipRegistration.class)) {
				if (env.shouldSkip(field.getAnnotation(SkipRegistration.class))) continue;
			}

			var modifiers = field.getModifiers();

			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers))) continue;

			try {
				var value = field.get(null);

				if (!env.getInterface().isInstance(value)) continue;

				env.getMethod().invoke(value);
			} catch (Exception e) {
				Astral.LOGGER.error(e.getLocalizedMessage());
			}
		}
	}

	/** Attempts to automatically register all values within the definition classes in the specified environment */
	private static <R extends Registerable> void run(RegisterEnv env) {
		for (var definitions : AutoRegisterImpl.DEFINITIONS_CLASSES) {
			AutoRegisterImpl.run(env, definitions);
		}
	}

	/** Automatically registers all mod content on the client environment */
	@Environment(EnvType.CLIENT)
	public static void runClient() {
		AutoRegisterImpl.run(RegisterEnv.CLIENT);
	}

	/** Automatically registers all mod content on the main environment */
	public static void runMain() {
		AutoRegisterImpl.run(RegisterEnv.MAIN);
	}

	/** Automatically registers all mod content on the server environment */
	@Environment(EnvType.SERVER)
	public static void runServer() {
		AutoRegisterImpl.run(RegisterEnv.SERVER);
	}

	/** Used to check annotation toggles */
	private static enum RegisterEnv {
		CLIENT(Registerable.Client.class, "registerClient"),
		MAIN(Registerable.Main.class, "registerMain"),
		SERVER(Registerable.Server.class, "registerServer");

		/** The environment's corresponding registerable interface */
		private final Class<? extends Registerable> INTERFACE;
		/** The name of the method used to register a value */
		private final String METHOD_NAME;

		private RegisterEnv(Class<? extends Registerable> iface, String methodName) {
			this.INTERFACE = iface;
			this.METHOD_NAME = methodName;
		}

		/** Returns the environment's corresponding registerable interface */
		public Class<? extends Registerable> getInterface() {
			return this.INTERFACE;
		}

		/** Returns the name of the method used to register a value */
		public String getMethodName() {
			return this.METHOD_NAME;
		}

		/** Returns the environment's registration method */
		public Method getMethod() throws NoSuchMethodException {
			return this.getInterface().getMethod(this.getMethodName());
		}

		/** Returns whether a value should be registered based on the provided annotation */
		public boolean shouldRegister(AutoRegister annotation) {
			switch (this) {
				case CLIENT:
					return annotation.client();
				case MAIN:
					return annotation.main();
				case SERVER:
					return annotation.server();
			}

			return false;
		}

		/** Returns whether a value should be skipped based on the provided annotation */
		public boolean shouldSkip(SkipRegistration annotation) {
			switch (this) {
				case CLIENT:
					return annotation.client();
				case MAIN:
					return annotation.main();
				case SERVER:
					return annotation.server();
			}

			return false;
		}
	}
}
