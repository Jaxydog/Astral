package dev.jaxydog.astral.content.block.custom;

import dev.jaxydog.astral.content.block.CustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/** Implements a randomizer block */
public class RandomizerBlock extends CustomBlock {

	/** Stores the active generated value until the `retain_state` property is false */
	public static final IntProperty ACTIVE_VALUE = IntProperty.of("active_value", 0, 15);
	/** Tells the block to reset its generated output when set to false */
	public static final BooleanProperty RETAIN_STATE = BooleanProperty.of("retain_state");
	/** Whether the block should be in "boolean mode", where it outputs either 0 or 15 at random */
	public static final BooleanProperty BOOLEAN_MODE = BooleanProperty.of("boolean_mode");

	public RandomizerBlock(String rawId, Settings settings) {
		super(rawId, settings);
		var state =
			this.stateManager.getDefaultState()
				.with(RandomizerBlock.ACTIVE_VALUE, 0)
				.with(RandomizerBlock.BOOLEAN_MODE, false)
				.with(RandomizerBlock.RETAIN_STATE, false);

		this.setDefaultState(state);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(RandomizerBlock.ACTIVE_VALUE, RandomizerBlock.BOOLEAN_MODE, RandomizerBlock.RETAIN_STATE);
	}

	@Override
	public ActionResult onUse(
		BlockState state,
		World world,
		BlockPos pos,
		PlayerEntity player,
		Hand hand,
		BlockHitResult hit
	) {
		if (!player.canModifyBlocks() || !player.isCreative()) return ActionResult.PASS;

		var toggled = state.cycle(RandomizerBlock.BOOLEAN_MODE).with(RandomizerBlock.RETAIN_STATE, false);

		world.setBlockState(pos, toggled);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, toggled));

		return ActionResult.SUCCESS;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if (!world.isReceivingRedstonePower(pos)) {
			this.discardValue(state, world, pos);

			return 0;
		}

		if (!state.get(RandomizerBlock.RETAIN_STATE)) {
			this.generateValue(state, world, pos);
		}

		return state.get(RandomizerBlock.ACTIVE_VALUE);
	}

	/** Generates a new active block value */
	private void generateValue(BlockState state, World world, BlockPos pos) {
		var value = state.get(RandomizerBlock.BOOLEAN_MODE)
			? (world.getRandom().nextBoolean() ? 0 : 15)
			: world.getRandom().nextBetween(0, 15);

		var update = state.with(RandomizerBlock.ACTIVE_VALUE, value).with(RandomizerBlock.RETAIN_STATE, true);

		world.setBlockState(pos, update);
	}

	/** Discards the block's active value */
	private void discardValue(BlockState state, World world, BlockPos pos) {
		var update = state.with(RandomizerBlock.RETAIN_STATE, false);

		world.setBlockState(pos, update);
	}
}
