package dev.jaxydog;

import dev.jaxydog.content.CustomContent;
import dev.jaxydog.utility.CurrencyUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The mod entrypoint */
public final class Astral implements ModInitializer {

	/** The mod's identifier */
	public static final String MOD_ID = "astral";
	/** The mod logger instance */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	/** The mod's default item group */
	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
		.displayName(Text.translatable(getId("default").toTranslationKey("itemGroup")))
		.icon(Items.NETHER_STAR::getDefaultStack)
		.build();

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM_GROUP, getId("default"), ITEM_GROUP);

		CustomContent.INSTANCE.registerMain();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CurrencyUtil.Loader());

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresentOrElse(mod -> {
			final String version = mod.getMetadata().getVersion().getFriendlyString();

			LOGGER.info(String.format("Astral v%s has loaded!", version));
		}, () -> {
			LOGGER.info("Astral has loaded!");
		});

		LOGGER.info("Thank you for playing with us <3");
	}

	/** Returns an identifier using the mod's namespace */
	public static Identifier getId(String path) {
		return Identifier.of(MOD_ID, path);
	}

}
