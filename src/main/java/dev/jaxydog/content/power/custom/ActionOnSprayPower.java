package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomPower;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnSprayPower extends CustomPower {

    private final int priority;
    private final int charges;
    private final @Nullable Consumer<Pair<World, ItemStack>> itemAction;
    private final @Nullable Predicate<ItemStack> itemCondition;
    private final @Nullable Consumer<Pair<Entity, Entity>> bientityAction;
    private final @Nullable Predicate<Pair<Entity, Entity>> bientityCondition;
    private final @Nullable Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private final @Nullable Predicate<CachedBlockPosition> blockCondition;

    public ActionOnSprayPower(
        PowerType<?> type,
        LivingEntity entity,
        int priority,
        int charges,
        @Nullable Consumer<Pair<World, ItemStack>> itemAction,
        @Nullable Predicate<ItemStack> itemCondition,
        @Nullable Consumer<Pair<Entity, Entity>> bientityAction,
        @Nullable Predicate<Pair<Entity, Entity>> bientityCondition,
        @Nullable Consumer<Triple<World, BlockPos, Direction>> blockAction,
        @Nullable Predicate<CachedBlockPosition> blockCondition
    ) {
        super(type, entity);

        this.priority = priority;
        this.charges = charges;
        this.itemAction = itemAction;
        this.itemCondition = itemCondition;
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.blockAction = blockAction;
        this.blockCondition = blockCondition;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getCharges() {
        return this.charges;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canUseItem(ItemStack stack) {
        return this.itemCondition == null || this.itemCondition.test(stack);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canSprayEntity(ItemStack stack, Entity target) {
        if (!this.canUseItem(stack) || this.bientityAction == null) return false;

        return this.bientityCondition == null || this.bientityCondition.test(new Pair<>(this.entity, target));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canSprayBlock(ItemStack stack, World world, BlockPos pos) {
        if (!this.canUseItem(stack) || this.blockAction == null) return false;

        return this.blockCondition == null || this.blockCondition.test(new CachedBlockPosition(world, pos, true));
    }

    public boolean onSprayEntity(ItemStack stack, Entity target) {
        if (this.bientityAction == null) return false;

        this.bientityAction.accept(new Pair<>(this.entity, target));

        if (this.itemAction != null) {
            this.itemAction.accept(new Pair<>(this.entity.getWorld(), stack));
        }

        return true;
    }

    public boolean onSprayBlock(ItemStack stack, World world, BlockPos pos, Direction direction) {
        if (this.blockAction == null) return false;

        this.blockAction.accept(new ImmutableTriple<>(world, pos, direction));

        if (this.itemAction != null) {
            this.itemAction.accept(new Pair<>(world, stack));
        }

        return true;
    }

}
