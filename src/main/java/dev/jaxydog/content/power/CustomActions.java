package dev.jaxydog.content.power;

import dev.jaxydog.content.power.action.RepeatAction;
import dev.jaxydog.register.ContentRegistrar;

/** Contains definitions for all custom actions */
@SuppressWarnings("unused")
public final class CustomActions extends ContentRegistrar {

    public static final RepeatAction REPEAT = new RepeatAction("repeat");

}
