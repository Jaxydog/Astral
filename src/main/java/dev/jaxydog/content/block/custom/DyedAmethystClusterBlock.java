package dev.jaxydog.content.block.custom;

import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class DyedAmethystClusterBlock extends DyedAmethystBlock
		implements Registerable.Client, Waterloggable {

	/** Whether the block is waterlogged */
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	/** The direction that the block is facing */
	public static final DirectionProperty FACING = Properties.FACING;

	// The block voxel shapes
	private final VoxelShape UP_SHAPE =
			Block.createCuboidShape(3.0D, 0.0F, 3.0D, 13.0D, 7.0D, 13.0D);
	private final VoxelShape DOWN_SHAPE =
			Block.createCuboidShape(3.0D, 9.0D, 3.0D, 13.0D, 16.0D, 13.0D);
	private final VoxelShape NORTH_SHAPE =
			Block.createCuboidShape(3.0D, 3.0D, 9.0D, 13.0D, 13.0D, 16.0D);
	private final VoxelShape SOUTH_SHAPE =
			Block.createCuboidShape(3.0D, 3.0D, 0.0F, 13.0D, 13.0D, 7.0D);
	private final VoxelShape EAST_SHAPE =
			Block.createCuboidShape(0.0F, 3.0D, 3.0D, 7.0D, 13.0D, 13.0D);
	private final VoxelShape WEST_SHAPE =
			Block.createCuboidShape(9.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D);

	public DyedAmethystClusterBlock(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
			ShapeContext context) {
		return switch (state.get(DyedAmethystClusterBlock.FACING)) {
			case UP -> this.UP_SHAPE;
			case DOWN -> this.DOWN_SHAPE;
			case NORTH -> this.NORTH_SHAPE;
			case SOUTH -> this.SOUTH_SHAPE;
			case EAST -> this.EAST_SHAPE;
			case WEST -> this.WEST_SHAPE;
			default -> this.UP_SHAPE;
		};
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		var direction = state.get(FACING);
		var blockPos = pos.offset(direction.getOpposite());

		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
			BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED).booleanValue()) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			return state;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		var worldAccess = ctx.getWorld();
		var blockPos = ctx.getBlockPos();

		return this.getDefaultState()
				.with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER)
				.with(FACING, ctx.getSide());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.get(WATERLOGGED).booleanValue()) {
			return Fluids.WATER.getStill(false);
		} else {
			return Fluids.EMPTY.getDefaultState();
		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING);
	}

	@Override
	public void registerClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(this, RenderLayer.getCutout());
	}

}
