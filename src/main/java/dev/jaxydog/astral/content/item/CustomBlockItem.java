package dev.jaxydog.content.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** An extension of a regular block item that provides additional functionality */
public class CustomBlockItem extends BlockItem implements Custom {

    /** The custom item's inner raw identifier */
    private final String idPath;
    private final @Nullable Supplier<RegistryKey<ItemGroup>> group;

    public CustomBlockItem(
        String idPath, Block block, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group
    ) {
        super(block, settings);

        this.idPath = idPath;
        this.group = group;
    }

    public CustomBlockItem(String idPath, Block block, Settings settings) {
        this(idPath, block, settings, null);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(this.getLoreTooltips(stack));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public RegistryKey<ItemGroup> getItemGroup() {
        return this.group == null ? Custom.super.getItemGroup() : this.group.get();
    }

    @Override
    public String getRegistryIdPath() {
        return this.idPath;
    }

}
