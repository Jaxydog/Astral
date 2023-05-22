package dev.jaxydog.astral;

import dev.jaxydog.astral.utility.AutoRegisterImpl;
import net.fabricmc.api.DedicatedServerModInitializer;

public class AstralServer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {
		AutoRegisterImpl.autoRegisterServer();
	}
}
