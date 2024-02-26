package dev.jaxydog.content.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** An extension of a regular armor item that provides additional functionality */
public class CustomArmorItem extends ArmorItem implements Custom {

    /** The custom armor item's inner raw identifier */
    private final String idPath;
    private final @Nullable Supplier<RegistryKey<ItemGroup>> group;

    public CustomArmorItem(
        String idPath,
        ArmorMaterial material,
        Type type,
        Settings settings,
        @Nullable Supplier<RegistryKey<ItemGroup>> group
    ) {
        super(material, type, settings);

        this.idPath = idPath;
        this.group = group;
    }

    public CustomArmorItem(String idPath, ArmorMaterial material, Type type, Settings settings) {
        this(idPath, material, type, settings, null);
    }

    /** Returns the total number of texture layers that the armor item expects to have */
    public int getTextureLayers() {
        return 1;
    }

    /** Returns the total number of texture layers that the armor item expects to have */
    @SuppressWarnings("unused")
    public int getTextureLayers(ItemStack stack) {
        return this.getTextureLayers();
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
