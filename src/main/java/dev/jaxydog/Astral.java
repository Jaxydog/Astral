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
	public static final Logger LOGGER = LoggerFactory.getLogger(Astral.MOD_ID);
	/** The mod's default item group */
	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
			.displayName(Text.translatable(Astral.getId("default").toTranslationKey("itemGroup")))
			.icon(() -> Items.NETHER_STAR.getDefaultStack()).build();

	/** Returns an identifier using the mod's namespace */
	public static final Identifier getId(String path) {
		return Identifier.of(Astral.MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM_GROUP, Astral.getId("default"), Astral.ITEM_GROUP);

		CustomContent.INSTANCE.registerMain();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new CurrencyUtil.Loader());

		FabricLoader.getInstance().getModContainer(Astral.MOD_ID).ifPresentOrElse(mod -> {
			final String version = mod.getMetadata().getVersion().getFriendlyString();
			final String text = String.format("Astral v%s has loaded!", version);

			Astral.LOGGER.info(text);
		}, () -> {
			Astral.LOGGER.info("Astral has loaded!");
		});

		Astral.LOGGER.info("Thank you for playing with us <3");
	}

}
