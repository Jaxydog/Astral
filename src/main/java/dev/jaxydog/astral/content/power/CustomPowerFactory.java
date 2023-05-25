package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.utility.register.Registerable;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/** An extension of a regular power factory that provides additional functionality */
public class CustomPowerFactory<P extends Power> extends PowerFactory<P> implements Registerable.Main {

	/** The custom power factory's inner raw identifier */
	private final String RAW_ID;

	public CustomPowerFactory(
		String rawId,
		SerializableData data,
		Function<io.github.apace100.calio.data.SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor
	) {
		super(Astral.getId(rawId), data, factoryConstructor);
		this.RAW_ID = rawId;
	}

	@Override
	public CustomPowerFactory<P> allowCondition() {
		return (CustomPowerFactory<P>) super.allowCondition();
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public Identifier getId() {
		return this.getSerializerId();
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();
	}
}
