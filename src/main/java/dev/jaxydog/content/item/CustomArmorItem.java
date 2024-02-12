package dev.jaxydog.content.item;

import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** An extension of a regular armor item that provides additional functionality */
public class CustomArmorItem extends ArmorItem implements Registered.Common {

    /** The custom armor item's inner raw identifier */
    private final String idPath;
    private final @Nullable CustomItemGroup group;

    public CustomArmorItem(
        String idPath, ArmorMaterial material, Type type, Settings settings, @Nullable CustomItemGroup group
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
    @SuppressWarnings({ "SameReturnValue", "unused" })
    public int getTextureLayers(ItemStack stack) {
        return this.getTextureLayers();
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        final String key = stack.getItem().getTranslationKey(stack) + ".lore_";
        int index = 0;

        while (I18n.hasTranslation(key + index)) {
            tooltip.add(Text.translatable(key + index).formatted(Formatting.GRAY));

            index += 1;
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public String getRegistryIdPath() {
        return this.idPath;
    }

    @Override
    public void register() {
        Registry.register(Registries.ITEM, this.getRegistryId(), this);

        final CustomItemGroup group = this.group == null ? CustomItemGroups.DEFAULT : this.group;

        ItemGroupEvents.modifyEntriesEvent(group.getRegistryKey()).register(g -> g.add(this));
    }

}
