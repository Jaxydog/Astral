package dev.jaxydog.content.power;

import dev.jaxydog.content.power.custom.RepeatAction;
import dev.jaxydog.register.ContentRegistrar;

/** Contains definitions for all custom actions */
public final class CustomActions extends ContentRegistrar {

	public static final RepeatAction REPEAT = new RepeatAction("repeat");

}
