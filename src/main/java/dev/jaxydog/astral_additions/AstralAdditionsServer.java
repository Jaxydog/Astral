package dev.jaxydog.astral_additions;

import dev.jaxydog.astral_additions.utility.AutoRegisterImpl;
import net.fabricmc.api.DedicatedServerModInitializer;

public class AstralAdditionsServer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {
		AutoRegisterImpl.autoRegisterServer();
	}
}
