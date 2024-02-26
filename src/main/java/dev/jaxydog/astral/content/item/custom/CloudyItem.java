package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.Colored;
import dev.jaxydog.astral.content.item.CustomItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CloudyItem extends CustomItem implements Cloudy, Colored {

    private static final double INCREASE_DELTA = 1D / 80D;
    private static final double DECREASE_DELTA = 1D / 160D;

    @SuppressWarnings("unused")
    public CloudyItem(String idPath, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group) {
        super(idPath, settings, group);
    }

    public CloudyItem(String idPath, Settings settings) {
        super(idPath, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(this.getStorminessText(stack));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public int getColor(ItemStack stack, int index) {
        return index == 0 ? this.getStorminessColor(stack) : 0xFF_FF_FF;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        this.updateStorminess(stack, entity, INCREASE_DELTA, DECREASE_DELTA);

        super.inventoryTick(stack, world, entity, slot, selected);
    }

}
