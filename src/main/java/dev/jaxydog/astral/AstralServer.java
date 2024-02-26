package dev.jaxydog.astral;

import dev.jaxydog.astral.content.CustomContent;
import net.fabricmc.api.DedicatedServerModInitializer;

/** The mod server entrypoint */
public final class AstralServer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {
		CustomContent.INSTANCE.registerServer();
	}

}
