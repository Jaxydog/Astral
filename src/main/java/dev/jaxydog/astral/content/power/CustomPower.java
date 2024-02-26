package dev.jaxydog.astral.content.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

/** Abstract extension of a regular power that provides additional functionality */
public abstract class CustomPower extends Power {

	public CustomPower(PowerType<?> type, LivingEntity entity) {
		super(type, entity);
	}

}
