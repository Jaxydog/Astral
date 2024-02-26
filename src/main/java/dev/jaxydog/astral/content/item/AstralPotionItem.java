package dev.jaxydog.astral.content.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class AstralPotionItem extends PotionItem implements Custom {

    private final String idPath;
    private final @Nullable Supplier<RegistryKey<ItemGroup>> group;

    public AstralPotionItem(String rawId, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group) {
        super(settings);

        this.idPath = rawId;
        this.group = group;
    }

    public AstralPotionItem(String rawId, Settings settings) {
        this(rawId, settings, null);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(this.getLoreTooltips(stack));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public RegistryKey<ItemGroup> getItemGroup() {
        return this.group == null ? ItemGroups.FOOD_AND_DRINK : this.group.get();
    }

    @Override
    public String getRegistryIdPath() {
        return this.idPath;
    }

    @Override
    public void register() {
        Registry.register(Registries.ITEM, this.getRegistryId(), this);
        BrewingRecipeRegistry.registerPotionType(this);
        ItemGroupEvents.modifyEntriesEvent(this.getItemGroup()).register(group -> Registries.POTION.forEach(potion -> {
            if (potion.equals(Potions.EMPTY)) return;

            final ItemStack stack = this.getDefaultStack();

            PotionUtil.setPotion(stack, potion);

            group.add(stack);
        }));
    }

}
