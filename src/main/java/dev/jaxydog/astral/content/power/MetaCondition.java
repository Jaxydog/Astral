package dev.jaxydog.astral.content.power;

import dev.jaxydog.astral.utility.register.Registerable;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;

/** Provides an interface for easily creating meta conditions */
public abstract class MetaCondition implements Registerable.Main {

	/** The meta condition's inner raw identifier */
	private final String RAW_ID;

	public MetaCondition(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Executes the meta condition */
	public abstract <T> void condition(SerializableData.Instance data, T value);

	/** Provides a condition factory that will be registered by the meta condition */
	public abstract <T> CustomConditionFactory<T> getFactory(
		SerializableDataType<CustomConditionFactory<T>.Instance> data,
		Supplier<Registry<ConditionFactory<T>>> registry
	);

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();

		this.getFactory(ApoliDataTypes.BIENTITY_CONDITION, () -> ApoliRegistries.BIENTITY_CONDITION).registerMain();
		this.getFactory(ApoliDataTypes.BIOME_CONDITION, () -> ApoliRegistries.BIOME_CONDITION).registerMain();
		this.getFactory(ApoliDataTypes.BLOCK_CONDITION, () -> ApoliRegistries.BLOCK_CONDITION).registerMain();
		this.getFactory(ApoliDataTypes.DAMAGE_CONDITION, () -> ApoliRegistries.DAMAGE_CONDITION).registerMain();
		this.getFactory(ApoliDataTypes.ENTITY_CONDITION, () -> ApoliRegistries.ENTITY_CONDITION).registerMain();
		this.getFactory(ApoliDataTypes.FLUID_CONDITION, () -> ApoliRegistries.FLUID_CONDITION).registerMain();
		this.getFactory(ApoliDataTypes.ITEM_CONDITION, () -> ApoliRegistries.ITEM_CONDITION).registerMain();
	}
}
