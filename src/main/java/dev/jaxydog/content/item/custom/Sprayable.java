package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.sound.CustomSoundEvents;
import dev.jaxydog.register.Registered;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface Sprayable extends Registered.Client {

    default boolean isEmptied(ItemStack stack) {
        return stack.getItem() instanceof Sprayable && stack.getDamage() >= stack.getMaxDamage();
    }

    default boolean canSpray(ItemStack stack) {
        return !this.isEmptied(stack);
    }

    default int getSprayCooldown(ItemStack stack) {
        return 8;
    }

    default SoundEvent getSpraySoundEvent(ItemStack stack) {
        return CustomSoundEvents.SPRAY_BOTTLE_USE;
    }

    default SoundCategory getSpraySoundCategory(ItemStack stack) {
        return SoundCategory.NEUTRAL;
    }

    default float getEmptyModel(ItemStack stack, World world, LivingEntity entity, int seed) {
        return this.isEmptied(stack) ? 1F : 0F;
    }

    default void onFilled(
        ItemStack stack, World world, BlockPos blockPos, @Nullable PlayerEntity player, int charges
    ) { }

    default void onSprayed(ItemStack stack, World world, @Nullable PlayerEntity player, int charges) { }

    default void onEmptied(ItemStack stack, World world, @Nullable PlayerEntity player) { }

    default void fill(ItemStack stack, World world, BlockPos blockPos, @Nullable PlayerEntity player, int charges) {
        if (!(stack.getItem() instanceof Sprayable)) return;

        stack.setDamage(Math.max(0, stack.getMaxDamage() - charges));

        if (player != null) {
            final double x = player.getX();
            final double y = player.getY();
            final double z = player.getZ();

            world.playSound(null, x, y, z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1F, 1F);
            world.emitGameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
        }

        this.onFilled(stack, world, blockPos, player, charges);
    }

    default void spray(ItemStack stack, World world, @Nullable PlayerEntity player, int charges) {
        if (!(stack.getItem() instanceof Sprayable)) return;

        if (player == null || !player.isCreative()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                stack.damage(charges, world.getRandom(), serverPlayer);
            } else {
                stack.damage(charges, world.getRandom(), null);
            }

            if (this.isEmptied(stack)) {
                this.onEmptied(stack, world, player);
            }
        }

        if (player != null) {
            player.getItemCooldownManager().set(stack.getItem(), this.getSprayCooldown(stack));
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));

            if (!player.isSilent()) {
                final SoundEvent soundEvent = this.getSpraySoundEvent(stack);
                final SoundCategory soundCategory = this.getSpraySoundCategory(stack);
                final float pitchVariance = (player.getRandom().nextFloat() - 0.5F) * 0.125F;

                player.playSound(soundEvent, soundCategory, 1F, 1F + pitchVariance);
            }
        }

        this.onSprayed(stack, world, player, charges);
    }

}
