package dev.jaxydog.content.item;

import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomPotionItem extends PotionItem implements Registered.Common {

    private final String idPath;
    private final @Nullable CustomItemGroup group;

    public CustomPotionItem(String rawId, Settings settings, @Nullable CustomItemGroup group) {
        super(settings);

        this.idPath = rawId;
        this.group = group;
    }

    public CustomPotionItem(String rawId, Settings settings) {
        this(rawId, settings, null);
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
        BrewingRecipeRegistry.registerPotionType(this);

        ItemGroupEvents.modifyEntriesEvent(this.group == null ? ItemGroups.FOOD_AND_DRINK : this.group.getRegistryKey())
            .register(group -> Registries.POTION.forEach(potion -> {
                if (potion.equals(Potions.EMPTY)) return;

                final ItemStack stack = this.getDefaultStack();

                PotionUtil.setPotion(stack, potion);

                group.add(stack);
            }));
    }

}
