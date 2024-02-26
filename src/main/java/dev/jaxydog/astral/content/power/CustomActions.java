package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.content.power.action.RepeatAction;
import dev.jaxydog.astral.register.ContentRegistrar;

/** Contains definitions for all custom actions */
@SuppressWarnings("unused")
public final class CustomActions extends ContentRegistrar {

    public static final RepeatAction REPEAT = new RepeatAction("repeat");

}
