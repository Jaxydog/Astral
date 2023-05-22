package dev.jaxydog.astral;

import dev.jaxydog.astral.utility.AutoRegisterImpl;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Astral implements ModInitializer {

	public static final String MOD_ID = "astral";
	public static final Logger LOGGER = LoggerFactory.getLogger(Astral.class.getName());

	public static Identifier getId(String path) {
		return new Identifier(Astral.MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		AutoRegisterImpl.autoRegisterMain();
		Astral.LOGGER.info("Astral has loaded! Thanks for playing with us <3");
	}
}
