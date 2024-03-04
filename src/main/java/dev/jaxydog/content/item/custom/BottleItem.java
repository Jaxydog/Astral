package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BottleItem extends CustomItem {

    @SuppressWarnings("unused")
    public BottleItem(String idPath, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group) {
        super(idPath, settings, group);
    }

    public BottleItem(String idPath, Settings settings) {
        super(idPath, settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return ItemUsage.consumeHeldItem(world, player, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        final @Nullable PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

        if (player != null && !player.isCreative()) stack.decrement(1);

        entity.emitGameEvent(GameEvent.DRINK);

        // This mimics the behavior of `PlayerEntity#eatFood` while allowing more customization.
        if (stack.isFood()) {
            if (player != null) {
                final float pitchVariance = world.getRandom().nextFloat() * 0.1F;

                player.getHungerManager().eat(stack.getItem(), stack);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                player.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5F, 0.9F + pitchVariance);

                // Usually, `CONSUME_ITEM` is triggered here, but we already trigger it later.
            }

            // Don't play the eating sound that would be on this line, as this is not food.
            entity.applyFoodEffects(stack, world, entity);

            // Usually, decrementing the stack and emitting a game event is handled here.
            // Instead, we handle it above this condition, as bottle items are always consumed.
        }

        if (player instanceof final ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        // Give the entity a bottle when drinking, unless they're a player in creative mode.
        if (player == null || !player.isCreative()) {
            if (stack.isEmpty()) return Items.GLASS_BOTTLE.getDefaultStack();

            if (player != null) player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
        }

        return stack;
    }

}
