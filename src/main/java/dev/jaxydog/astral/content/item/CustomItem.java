package dev.jaxydog.astral.content.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** An extension of a regular item that provides additional functionality */
public class CustomItem extends Item implements Custom {

    /** The custom item's inner raw identifier */
    private final String idPath;
    private final @Nullable Supplier<RegistryKey<ItemGroup>> group;

    public CustomItem(String idPath, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group) {
        super(settings);

        this.idPath = idPath;
        this.group = group;
    }

    public CustomItem(String idPath, Settings settings) {
        this(idPath, settings, null);
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
    public String getRegistryPath() {
        return this.idPath;
    }

}
