package dev.jaxydog.content.power;

import org.apache.commons.lang3.tuple.Triple;
import dev.jaxydog.utility.register.Registerable;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableData.Instance;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/** Abstract class for implementing actions with multiple data types */
public abstract class CustomMetaAction implements Registerable.Main {

	/** The custom meta action's inner identifier */
	private final String RAW_ID;

	public CustomMetaAction(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Executes the action */
	public abstract <T> void execute(Instance data, T value);

	/** Returns the action's factory */
	public abstract <T> CustomActionFactory<T> factory(
			SerializableDataType<ActionFactory<T>.Instance> type);

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		this.<Pair<Entity, Entity>>factory(ApoliDataTypes.BIENTITY_ACTION)
				.register(ApoliRegistries.BIENTITY_ACTION);
		this.<Triple<World, BlockPos, Direction>>factory(ApoliDataTypes.BLOCK_ACTION)
				.register(ApoliRegistries.BLOCK_ACTION);
		this.<Entity>factory(ApoliDataTypes.ENTITY_ACTION).register(ApoliRegistries.ENTITY_ACTION);
		this.<Pair<World, ItemStack>>factory(ApoliDataTypes.ITEM_ACTION)
				.register(ApoliRegistries.ITEM_ACTION);
	}

}
