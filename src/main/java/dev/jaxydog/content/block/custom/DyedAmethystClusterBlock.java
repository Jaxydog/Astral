package dev.jaxydog.content.block.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class DyedAmethystClusterBlock extends DyedAmethystBlock implements Registered.Client, Waterloggable {

	public static final TagKey<Item> AMETHYST_CLUSTERS = TagKey.of(Registries.ITEM.getKey(),
		Astral.getId("amethyst_blocks")
	);

	/** Whether the block is waterlogged */
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	/** The direction that the block is facing */
	public static final DirectionProperty FACING = Properties.FACING;

	// The block voxel shapes
	private final VoxelShape UP_SHAPE = Block.createCuboidShape(3D, 0F, 3D, 13D, 7D, 13D);
	private final VoxelShape DOWN_SHAPE = Block.createCuboidShape(3D, 9D, 3D, 13D, 16D, 13D);
	private final VoxelShape NORTH_SHAPE = Block.createCuboidShape(3D, 3D, 9D, 13D, 13D, 16D);
	private final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(3D, 3D, 0F, 13D, 13D, 7D);
	private final VoxelShape EAST_SHAPE = Block.createCuboidShape(0F, 3D, 3D, 7D, 13D, 13D);
	private final VoxelShape WEST_SHAPE = Block.createCuboidShape(9D, 3D, 3D, 16D, 13D, 13D);

	public DyedAmethystClusterBlock(String rawId, DyeColor color, Settings settings) {
		super(rawId, color, settings);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state,
		Direction direction,
		BlockState neighborState,
		WorldAccess world,
		BlockPos pos,
		BlockPos neighborPos
	) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			return state;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.get(WATERLOGGED)) {
			return Fluids.WATER.getStill(false);
		} else {
			return Fluids.EMPTY.getDefaultState();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		final Direction direction = state.get(FACING);
		final BlockPos blockPos = pos.offset(direction.getOpposite());

		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(DyedAmethystClusterBlock.FACING)) {
			case UP -> this.UP_SHAPE;
			case DOWN -> this.DOWN_SHAPE;
			case NORTH -> this.NORTH_SHAPE;
			case SOUTH -> this.SOUTH_SHAPE;
			case EAST -> this.EAST_SHAPE;
			case WEST -> this.WEST_SHAPE;
		};
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		final World world = ctx.getWorld();
		final BlockPos blockPos = ctx.getBlockPos();
		final boolean waterlogged = world.getFluidState(blockPos).getFluid() == Fluids.WATER;

		return this.getDefaultState().with(WATERLOGGED, waterlogged).with(FACING, ctx.getSide());
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
