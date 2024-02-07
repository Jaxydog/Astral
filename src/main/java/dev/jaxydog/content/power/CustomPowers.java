package dev.jaxydog.content.power;

import dev.jaxydog.content.data.CustomData;
import dev.jaxydog.content.data.ScaleOperation;
import dev.jaxydog.content.power.custom.*;
import dev.jaxydog.register.ContentRegistrar;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;

/** Contains definitions for all custom powers */
@SuppressWarnings("unused")
public final class CustomPowers extends ContentRegistrar {

    public static final CustomPowerFactory<TickingCooldownPower> TICKING_COOLDOWN = TickingCooldownPower.createTickingCooldownFactory();

    public static final CustomPowerFactory<ActionOnKeyPower> ACTION_ON_KEY = ActionOnKeyPower.createActionOnKeyFactory();

    public static final CustomPowerFactory<ModifyScalePower> MODIFY_SCALE = ModifyScalePower.getFactory();

    @SuppressWarnings("removal") // To be removed in tandem.
    @Deprecated(since = "1.7.0", forRemoval = true)
    public static final CustomPowerFactory<ScalePower> SCALE = new CustomPowerFactory<ScalePower>(
        "scale",
        new SerializableData().add("width", SerializableDataTypes.FLOAT, 1F)
            .add("height", SerializableDataTypes.FLOAT, 1F)
            .add("reach", SerializableDataTypes.FLOAT, 1F)
            .add("motion", SerializableDataTypes.FLOAT, 1F)
            .add("jump", SerializableDataTypes.FLOAT, 1F)
            .add("reset_on_lost", SerializableDataTypes.BOOLEAN, true)
            .add("operation", CustomData.SCALE_OPERATION, ScaleOperation.MULTIPLICATIVE),
        data -> (type, entity) -> new ScalePower(
            type,
            entity,
            data.getFloat("width"),
            data.getFloat("height"),
            data.getFloat("reach"),
            data.getFloat("motion"),
            data.getFloat("jump"),
            data.getBoolean("reset_on_lost"),
            data.get("operation")
        )
    ).allowCondition();

    public static final CustomPowerFactory<ActionOnSprayPower> ACTION_ON_SPRAY = new CustomPowerFactory<ActionOnSprayPower>(
        "action_on_spray",
        new SerializableData().add("priority", SerializableDataTypes.INT, 0)
            .add("charges", SerializableDataTypes.INT, 1)
            .add("item_action", ApoliDataTypes.ITEM_ACTION, null)
            .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
            .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
            .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
            .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
            .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
        data -> (type, entity) -> new ActionOnSprayPower(
            type,
            entity,
            data.getInt("priority"),
            data.getInt("charges"),
            data.get("item_action"),
            data.get("item_condition"),
            data.get("bientity_action"),
            data.get("bientity_condition"),
            data.get("block_action"),
            data.get("block_condition")
        )
    ).allowCondition();

}
