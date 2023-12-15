package dev.jaxydog.content.power;

import dev.jaxydog.utility.register.Registerable;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

/** Abstract class for implementing conditions with multiple data types */
public abstract class CustomMetaCondition implements Registerable.Main {

	/** The custom meta condition's inner raw identifier */
	private final String RAW_ID;

	public CustomMetaCondition(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Checks the condition */
	public abstract <T> boolean check(Instance data, T value);

	/** Returns the condition's factory */
	public abstract <T> CustomConditionFactory<T> factory(SerializableDataType<ConditionFactory<T>.Instance> type);

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		this.<Pair<Entity, Entity>>factory(ApoliDataTypes.BIENTITY_CONDITION)
			.register(ApoliRegistries.BIENTITY_CONDITION);
		this.<CachedBlockPosition>factory(ApoliDataTypes.BLOCK_CONDITION)
			.register(ApoliRegistries.BLOCK_CONDITION);
		this.<Entity>factory(ApoliDataTypes.ENTITY_CONDITION)
			.register(ApoliRegistries.ENTITY_CONDITION);
		this.<ItemStack>factory(ApoliDataTypes.ITEM_CONDITION)
			.register(ApoliRegistries.ITEM_CONDITION);
	}

}
