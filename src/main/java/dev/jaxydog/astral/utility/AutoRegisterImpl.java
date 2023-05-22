package dev.jaxydog.astral.utility;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.content.block.CustomBlocks;
import dev.jaxydog.astral.content.effect.CustomStatusEffects;
import dev.jaxydog.astral.content.enchantment.CustomEnchantments;
import dev.jaxydog.astral.content.item.CustomItems;
import java.lang.reflect.Modifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Provides functionality for content auto-registration */
public class AutoRegisterImpl {

	/** The classes for which to automatically check for registerable values */
	public static final Class<?>[] DEFINITION_CLASSES = new Class<?>[] {
		CustomBlocks.class,
		CustomItems.class,
		CustomEnchantments.class,
		CustomStatusEffects.class,
	};

	/** Used to check annotation toggles */
	private enum RegisterType {
		CLIENT,
		MAIN,
		SERVER;

		/** Returns whether values should be registered */
		public boolean shouldRegister(AutoRegister annotation) {
			switch (this) {
				case CLIENT:
					return annotation.client();
				case MAIN:
					return annotation.main();
				case SERVER:
					return annotation.server();
				default:
					return false;
			}
		}

		/** Returns whether registration should be skipped */
		public boolean shouldSkip(SkipRegister annotation) {
			switch (this) {
				case CLIENT:
					return annotation.client();
				case MAIN:
					return annotation.main();
				case SERVER:
					return annotation.server();
				default:
					return false;
			}
		}
	}

	/** Generic method for registering content automatically */
	private static <R extends Registerable> void autoRegister(Class<R> registerable, String method, RegisterType type) {
		for (var definitions : AutoRegisterImpl.DEFINITION_CLASSES) {
			if (!definitions.isAnnotationPresent(AutoRegister.class)) continue;
			if (!type.shouldRegister(definitions.getAnnotation(AutoRegister.class))) continue;

			for (var field : definitions.getFields()) {
				if (!Modifier.isStatic(field.getModifiers())) continue;
				if (field.isAnnotationPresent(SkipRegister.class)) {
					if (type.shouldSkip(field.getAnnotation(SkipRegister.class))) continue;
				}

				try {
					var value = field.get(null);

					if (registerable.isInstance(value)) {
						registerable.getMethod(method).invoke(value);
					}
				} catch (Exception e) {
					Astral.LOGGER.error(e.getLocalizedMessage());
				}
			}
		}
	}

	/** Automatically registers all mod content in the client environment */
	@Environment(EnvType.CLIENT)
	public static void autoRegisterClient() {
		AutoRegisterImpl.autoRegister(Registerable.Client.class, "registerClient", RegisterType.CLIENT);
	}

	/** Automatically registers all mod content in the main environment */
	public static void autoRegisterMain() {
		AutoRegisterImpl.autoRegister(Registerable.Main.class, "registerMain", RegisterType.MAIN);
	}

	/** Automatically registers all mod content in the server environment */
	@Environment(EnvType.SERVER)
	public static void autoRegisterServer() {
		AutoRegisterImpl.autoRegister(Registerable.Server.class, "registerServer", RegisterType.SERVER);
	}
}
