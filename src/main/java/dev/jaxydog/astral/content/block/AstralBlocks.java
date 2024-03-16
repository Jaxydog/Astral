package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.content.block.custom.DyeableAmethystBlock;
import dev.jaxydog.astral.content.block.custom.DyeableAmethystClusterBlock;
import dev.jaxydog.astral.content.block.custom.DyeableAmethystClusterBlock.Variant;
import dev.jaxydog.astral.content.block.custom.DyeableBuddingAmethystBlock;
import dev.jaxydog.astral.content.block.custom.RandomizerBlock;
import dev.jaxydog.astral.datagen.TagGenerator;
import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.DyeMap;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;

/** Contains definitions for all custom blocks */
public final class AstralBlocks extends ContentRegistrar {

    public static final DyeMap<DyeableAmethystBlock> DYEABLE_AMETHYST_BLOCKS = new DyeMap<>("amethyst_block",
        (rawId, color) -> new DyeableAmethystBlock(rawId, Settings.copy(Blocks.AMETHYST_BLOCK), color)
    );
    public static final DyeMap<DyeableBuddingAmethystBlock> DYEABLE_BUDDING_AMETHYST_BLOCKS = new DyeMap<>(
        "budding_amethyst",
        (rawId, color) -> new DyeableBuddingAmethystBlock(rawId, Settings.copy(Blocks.BUDDING_AMETHYST), color)
    );
    public static final DyeMap<DyeableAmethystClusterBlock> DYEABLE_AMETHYST_CLUSTERS = new DyeMap<>("amethyst_cluster",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.AMETHYST_CLUSTER).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.CLUSTER);
        }
    );
    public static final DyeMap<DyeableAmethystClusterBlock> DYEABLE_LARGE_AMETHYST_BUDS = new DyeMap<>("large_amethyst_bud",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.LARGE_AMETHYST_BUD).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.LARGE_BUD);
        }
    );
    public static final DyeMap<DyeableAmethystClusterBlock> DYEABLE_MEDIUM_AMETHYST_BUDS = new DyeMap<>("medium_amethyst_bud",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.MEDIUM_AMETHYST_BUD).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.MEDIUM_BUD);
        }
    );
    public static final DyeMap<DyeableAmethystClusterBlock> DYEABLE_SMALL_AMETHYST_BUDS = new DyeMap<>("small_amethyst_bud",
        (rawId, color) -> {
            final Settings settings = Settings.copy(Blocks.SMALL_AMETHYST_BUD).mapColor(color);

            return new DyeableAmethystClusterBlock(rawId, settings, color, Variant.SMALL_BUD);
        }
    );

    public static final RandomizerBlock RANDOMIZER = new RandomizerBlock("randomizer",
        Settings.copy(Blocks.IRON_BLOCK)
    );

    @Override
    public void generate() {
        super.generate();

        TagGenerator.getInstance().generate(DyeableAmethystBlock.AMETHYST_BLOCKS, b -> b.add(Blocks.AMETHYST_BLOCK));
        TagGenerator.getInstance()
            .generate(DyeableBuddingAmethystBlock.BUDDING_AMETHYSTS, b -> b.add(Blocks.BUDDING_AMETHYST));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.AMETHYST_CLUSTERS, b -> b.add(Blocks.AMETHYST_CLUSTER));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.LARGE_AMETHYST_BUDS, b -> b.add(Blocks.LARGE_AMETHYST_BUD));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.MEDIUM_AMETHYST_BUDS, b -> b.add(Blocks.MEDIUM_AMETHYST_BUD));
        TagGenerator.getInstance()
            .generate(DyeableAmethystClusterBlock.SMALL_AMETHYST_BUDS, b -> b.add(Blocks.SMALL_AMETHYST_BUD));
    }

}
