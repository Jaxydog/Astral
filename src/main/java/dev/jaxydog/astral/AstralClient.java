package dev.jaxydog.astral;

import dev.jaxydog.astral.utility.AutoRegisterImpl;
import net.fabricmc.api.ClientModInitializer;

public class AstralClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoRegisterImpl.autoRegisterClient();
	}
}
