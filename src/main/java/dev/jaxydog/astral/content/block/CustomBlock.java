package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.register.Registered;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** An extension of a regular block that provides additional functionality */
public class CustomBlock extends Block implements Registered.Common {

    /** The custom block's inner raw identifier */
    private final String RAW_ID;

    public CustomBlock(String rawId, Settings settings) {
        super(settings);

        this.RAW_ID = rawId;
    }

    @Override
    public String getRegistryPath() {
        return this.RAW_ID;
    }

    @Override
    public void register() {
        Registry.register(Registries.BLOCK, this.getRegistryId(), this);
    }

}
