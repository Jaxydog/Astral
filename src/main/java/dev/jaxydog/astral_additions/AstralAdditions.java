package dev.jaxydog.astral_additions;

import dev.jaxydog.astral_additions.utility.AutoRegisterImpl;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstralAdditions implements ModInitializer {

	public static final String MOD_ID = "astral_additions";
	public static final Logger LOGGER = LoggerFactory.getLogger(AstralAdditions.class.getName());

	public static Identifier getId(String path) {
		return new Identifier(AstralAdditions.MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		AutoRegisterImpl.autoRegisterMain();
		AstralAdditions.LOGGER.info("Astral additions has loaded! Thanks for playing with us <3");
	}
}
