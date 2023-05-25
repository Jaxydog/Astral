package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.content.power.custom.LoopAction;
import dev.jaxydog.astral.utility.register.AutoRegister;

/** Contains definitions for all custom actions */
@AutoRegister
public final class CustomActions {

	public static final LoopAction LOOP = new LoopAction("loop");

	private CustomActions() {}
}
