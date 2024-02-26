package dev.jaxydog.content.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ChocolateMilkItem extends BottleItem {

    @SuppressWarnings("unused")
    public ChocolateMilkItem(String idPath, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group) {
        super(idPath, settings, group);
    }

    public ChocolateMilkItem(String idPath, Settings settings) {
        super(idPath, settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        if (entity != null && !world.isClient()) {
            SuspiciousStewItem.forEachEffect(stack, entity::addStatusEffect);
        }

        return super.finishUsing(stack, world, entity);
    }

}
