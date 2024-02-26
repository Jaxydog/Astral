package dev.jaxydog;

import dev.jaxydog.content.CustomContent;
import dev.jaxydog.utility.CurrencyUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/** The mod entrypoint */
public final class Astral implements ModInitializer {

    /** The mod's identifier */
    public static final String MOD_ID = "astral";
    /** The mod logger instance */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CustomContent.INSTANCE.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CurrencyUtil.Loader());

        getMetadata().ifPresent(metadata -> {
            final String name = metadata.getName();
            final String version = metadata.getVersion().getFriendlyString();

            LOGGER.info("{} v{} has loaded! Thank you for playing with us <3", name, version);
        });
    }

    /** Returns an identifier using the mod's namespace */
    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }

    /** Returns the metadata for this mod. */
    public static Optional<ModMetadata> getMetadata() {
        return FabricLoader.getInstance().getModContainer(MOD_ID).map(ModContainer::getMetadata);
    }

}
