/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2024 Jaxydog
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.jaxydog.astral.content.power.custom;

import dev.jaxydog.astral.content.power.CustomPower;
import dev.jaxydog.astral.content.power.CustomPowerFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Runs an action when the holding entity is sprayed.
 * <p>
 * This is the inverse of {@link ActionOnSprayPower}.
 *
 * @author Jaxydog
 */
public class ActionWhenSprayedPower extends CustomPower {

    private final int priority;
    private final int charges;
    private final @Nullable Consumer<Pair<Entity, Entity>> bientityAction;
    private final @Nullable Predicate<Pair<Entity, Entity>> bientityCondition;
    private final @Nullable Consumer<Pair<World, ItemStack>> itemAction;
    private final @Nullable Predicate<ItemStack> itemCondition;

    public ActionWhenSprayedPower(
        PowerType<?> type,
        LivingEntity entity,
        int priority,
        int charges,
        @Nullable Consumer<Pair<Entity, Entity>> bientityAction,
        @Nullable Predicate<Pair<Entity, Entity>> bientityCondition,
        @Nullable Consumer<Pair<World, ItemStack>> itemAction,
        @Nullable Predicate<ItemStack> itemCondition
    ) {
        super(type, entity);

        this.priority = priority;
        this.charges = charges;
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.itemAction = itemAction;
        this.itemCondition = itemCondition;
    }

    /**
     * Returns the power factory associated with this power type.
     *
     * @return The power factory.
     */
    public static CustomPowerFactory<ActionWhenSprayedPower> getFactory() {
        return new CustomPowerFactory<ActionWhenSprayedPower>(
            "action_when_sprayed",
            new SerializableData().add("priority", SerializableDataTypes.INT, 0)
                .add("charges", SerializableDataTypes.INT, 1)
                .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                .add("item_action", ApoliDataTypes.ITEM_ACTION, null)
                .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null),
            data -> (type, entity) -> new ActionWhenSprayedPower(
                type,
                entity,
                data.getInt("priority"),
                data.getInt("charges"),
                data.get("bientity_action"),
                data.get("bientity_condition"),
                data.get("item_action"),
                data.get("item_condition")
            )
        ).allowCondition();
    }

    /**
     * Returns the power's execution priority, which is used to determine when this power is triggered compared to other
     * held powers.
     *
     * @return The execution priority.
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Returns the total number of charges to consume when a spray is successful.
     *
     * @return The expended charges.
     */
    public int getCharges() {
        return this.charges;
    }

    /**
     * Returns whether the provided item stack is considered valid for spraying through this power.
     *
     * @param stack The item stack.
     *
     * @return The validity of the stack.
     */
    public boolean canUseItem(ItemStack stack) {
        return this.itemCondition == null || this.itemCondition.test(stack);
    }

    /**
     * Returns whether this entity may be sprayed using the given stack and actor entity.
     *
     * @param actor The actor entity.
     * @param stack The item stack.
     *
     * @return Whether this entity may be sprayed.
     */
    public boolean canBeSprayed(@Nullable Entity actor, ItemStack stack) {
        if (!this.canUseItem(stack)) return false;
        if (this.bientityCondition == null) return true;

        return actor != null && this.bientityCondition.test(new Pair<>(actor, this.entity));
    }

    /**
     * Attempts to spray the entity that holds this power, returning whether the attempt was successful.
     *
     * @param actor The actor entity.
     * @param stack The item stack.
     *
     * @return Whether the entity was sprayed.
     */
    public boolean onSprayed(@Nullable Entity actor, ItemStack stack) {
        if (this.bientityAction == null) return false;

        this.bientityAction.accept(new Pair<>(actor, this.entity));

        if (this.itemAction != null) {
            this.itemAction.accept(new Pair<>(this.entity.getWorld(), stack));
        }

        return true;
    }

}
