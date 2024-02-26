package dev.jaxydog.astral;

import dev.jaxydog.astral.content.CustomContent;
import net.fabricmc.api.ClientModInitializer;

/** The mod client entrypoint */
public final class AstralClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomContent.INSTANCE.registerClient();
    }

}
