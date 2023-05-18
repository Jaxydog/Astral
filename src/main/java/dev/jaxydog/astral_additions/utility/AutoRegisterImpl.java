package dev.jaxydog.astral_additions.utility;

import dev.jaxydog.astral_additions.AstralAdditions;
import dev.jaxydog.astral_additions.content.block.CustomBlocks;
import dev.jaxydog.astral_additions.content.effect.CustomStatusEffects;
import dev.jaxydog.astral_additions.content.item.CustomItems;
import java.lang.reflect.Modifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Provides functionality for content auto-registration */
public class AutoRegisterImpl {

	/** The classes for which to automatically check for registerable values */
	private static final Class<?>[] DEFINITION_CLASSES = new Class<?>[] {
		CustomBlocks.class,
		CustomItems.class,
		CustomStatusEffects.class,
	};

	/** Generic method for registering content automatically */
	private static <R extends Registerable> void autoRegister(Class<R> registerable, String method) {
		for (var contentClass : AutoRegisterImpl.DEFINITION_CLASSES) {
			if (!contentClass.isAnnotationPresent(AutoRegister.class)) continue;

			for (var field : contentClass.getFields()) {
				if (field.isAnnotationPresent(SkipRegister.class)) continue;
				if (!Modifier.isStatic(field.getModifiers())) continue;

				try {
					var value = field.get(null);

					if (registerable.isInstance(value)) {
						registerable.getMethod(method).invoke(value);
					}
				} catch (Exception e) {
					AstralAdditions.LOGGER.error(e.getLocalizedMessage());
				}
			}
		}
	}

	/** Automatically registers all mod content in the client environment */
	@Environment(EnvType.CLIENT)
	public static void autoRegisterClient() {
		AutoRegisterImpl.autoRegister(Registerable.Client.class, "registerClient");
	}

	/** Automatically registers all mod content in the main environment */
	public static void autoRegisterMain() {
		AutoRegisterImpl.autoRegister(Registerable.Main.class, "registerMain");
	}

	/** Automatically registers all mod content in the server environment */
	@Environment(EnvType.SERVER)
	public static void autoRegisterServer() {
		AutoRegisterImpl.autoRegister(Registerable.Server.class, "registerServer");
	}
}
