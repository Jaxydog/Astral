package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.content.data.CustomData;
import dev.jaxydog.astral.content.power.custom.*;
import dev.jaxydog.astral.register.ContentRegistrar;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;

/** Contains definitions for all custom powers */
@SuppressWarnings("unused")
public final class CustomPowers extends ContentRegistrar {

    public static final CustomPowerFactory<TickingCooldownPower> TICKING_COOLDOWN = TickingCooldownPower.getCooldownFactory();

    public static final CustomPowerFactory<ActionOnKeyPower> ACTION_ON_KEY = ActionOnKeyPower.getActionFactory();

    public static final CustomPowerFactory<ActionOnSprayPower> ACTION_ON_SPRAY = ActionOnSprayPower.getFactory();

    public static final CustomPowerFactory<ActionWhenSprayedPower> ACTION_WHEN_SPRAYED = ActionWhenSprayedPower.getFactory();

    public static final CustomPowerFactory<ModifyScalePower> MODIFY_SCALE = ModifyScalePower.getFactory();

    @SuppressWarnings("removal") // To be removed in tandem.
    @Deprecated(since = "1.7.0", forRemoval = true)
    public static final CustomPowerFactory<ScalePower> SCALE = new CustomPowerFactory<ScalePower>("scale",
        new SerializableData().add("width", SerializableDataTypes.FLOAT, 1F)
            .add("height", SerializableDataTypes.FLOAT, 1F)
            .add("reach", SerializableDataTypes.FLOAT, 1F)
            .add("motion", SerializableDataTypes.FLOAT, 1F)
            .add("jump", SerializableDataTypes.FLOAT, 1F)
            .add("reset_on_lost", SerializableDataTypes.BOOLEAN, true)
            .add("operation",
                CustomData.SCALE_OPERATION,
                dev.jaxydog.astral.content.data.ScaleOperation.MULTIPLICATIVE
            ),
        data -> (type, entity) -> new ScalePower(type,
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

}
