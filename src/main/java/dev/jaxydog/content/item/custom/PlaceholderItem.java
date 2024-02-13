package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Map;

public class PlaceholderItem extends CustomItem implements Customized, Equipment {

    private static final Map<Integer, String> TRANSLATION_KEYS = new Object2ObjectOpenHashMap<>();

    public PlaceholderItem(String rawId, Settings settings) {
        super(rawId, settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        final int data = this.getCustomModelData(stack);

        if (data == 0) return super.getTranslationKey(stack);

        // Cache translation keys to reduce repeated computation.
        if (!TRANSLATION_KEYS.containsKey(data)) {
            TRANSLATION_KEYS.put(data, super.getTranslationKey(stack) + "." + data);
        }

        return TRANSLATION_KEYS.get(data);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    // Override to prevent this item from appearing within any item groups.
    @Override
    public void register() {
        Registry.register(Registries.ITEM, this.getRegistryId(), this);
    }

}
