package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomPowerFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnKeyPower extends ConditionedCooldownPower implements Active {

    private final Consumer<Entity> activeFunction;

    private Key key;

    public ActionOnKeyPower(
        PowerType<?> type,
        LivingEntity entity,
        int duration,
        HudRender hudRender,
        @Nullable Predicate<Entity> tickCondition,
        Consumer<Entity> activeFunction
    ) {
        super(type, entity, duration, hudRender, tickCondition);

        this.activeFunction = activeFunction;
    }

    public static CustomPowerFactory<ActionOnKeyPower> createActionOnKeyFactory() {
        return new CustomPowerFactory<ActionOnKeyPower>(
            "action_on_key",
            new SerializableData().add("cooldown", SerializableDataTypes.INT)
                .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                .add("tick_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                .add("entity_action", ApoliDataTypes.ENTITY_ACTION)
                .add("key", ApoliDataTypes.KEY, new Key()),
            data -> (type, player) -> {
                final ActionOnKeyPower power = new ActionOnKeyPower(
                    type,
                    player,
                    data.getInt("cooldown"),
                    data.get("hud_render"),
                    data.get("tick_condition"),
                    data.get("entity_action")
                );

                power.setKey(data.get("key"));

                return power;
            }
        ).allowCondition();
    }

    @Override
    public void onUse() {
        if (this.canUse()) {
            this.activeFunction.accept(this.entity);
            this.use();
        }
    }

    @Override
    public Key getKey() {
        return this.key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

}
