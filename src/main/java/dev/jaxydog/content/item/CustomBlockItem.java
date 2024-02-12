package dev.jaxydog.content.item;

import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** An extension of a regular block item that provides additional functionality */
public class CustomBlockItem extends BlockItem implements Registered.Common {

    /** The custom item's inner raw identifier */
    private final String idPath;
    private final @Nullable CustomItemGroup group;

    public CustomBlockItem(String idPath, Block block, Settings settings, @Nullable CustomItemGroup group) {
        super(block, settings);

        this.idPath = idPath;
        this.group = group;
    }

    public CustomBlockItem(String idPath, Block block, Settings settings) {
        this(idPath, block, settings, null);
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
