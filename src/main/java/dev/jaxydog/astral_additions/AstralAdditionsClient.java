package dev.jaxydog.astral_additions;

import dev.jaxydog.astral_additions.utility.AutoRegisterImpl;
import net.fabricmc.api.ClientModInitializer;

public class AstralAdditionsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoRegisterImpl.autoRegisterClient();
	}
}
