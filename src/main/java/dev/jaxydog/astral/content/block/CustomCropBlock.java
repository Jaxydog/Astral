package dev.jaxydog.astral.content.block;

import dev.jaxydog.astral.utility.Registerable;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

/** An extension of a regular crop block that provides additional functionality */
public abstract class CustomCropBlock extends CropBlock implements Registerable.Main, Registerable.Client {

	/** The custom crop block's inner raw identifier */
	private final String RAW_ID;
	/** The custom crop block's configuration settings */
	private final Config CONFIG;

	public CustomCropBlock(String rawId, Settings settings, Config config) {
		super(settings);
		this.RAW_ID = rawId;
		this.CONFIG = config;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(this.getAgeProperty());
	}

	@Override
	public IntProperty getAgeProperty() {
		return this.CONFIG.getAgeProperty();
	}

	@Override
	public int getMaxAge() {
		return this.CONFIG.getGrowthStages();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.CONFIG.getShapeList().get(this.getAge(state));
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	protected ItemConvertible getSeedsItem() {
		return this.CONFIG.getSeedsItem();
	}

	@Override
	public void registerClient() {
		Client.super.registerClient();
		BlockRenderLayerMap.INSTANCE.putBlock(this, RenderLayer.getCutout());
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();
		Registry.register(Registries.BLOCK, this.getId(), this);
	}

	/** Provides configuration options for custom crops */
	public static class Config {

		private final ItemConvertible SEEDS_ITEM;
		private IntProperty ageProperty = Properties.AGE_3;
		private double minHeight = 2.0D;
		private double maxHeight = 16.0D;

		@Nullable
		private List<VoxelShape> shapeList = null;

		public Config(ItemConvertible seedsItem) {
			this.SEEDS_ITEM = seedsItem;
		}

		private void generateShapes() {
			var shapeList = new ArrayList<VoxelShape>(this.getGrowthStages());
			var scale = (this.maxHeight - this.minHeight) / this.getGrowthStages();

			for (var index = 0; index < this.getGrowthStages(); index++) {
				var height = this.minHeight + (scale * index);
				var shape = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, height, 16.0D);

				shapeList.add(shape);
			}

			this.shapeList = shapeList;
		}

		public IntProperty getAgeProperty() {
			return this.ageProperty;
		}

		public int getGrowthStages() {
			return this.ageProperty.getValues().size();
		}

		public ItemConvertible getSeedsItem() {
			return this.SEEDS_ITEM;
		}

		public List<VoxelShape> getShapeList() {
			if (this.shapeList == null) this.generateShapes();

			return this.shapeList;
		}

		public Config withHeightRange(double minHeight, double maxHeight) {
			this.minHeight = minHeight;
			this.maxHeight = maxHeight;
			this.generateShapes();

			return this;
		}

		public Config withShapeList(VoxelShape... shapes) {
			this.shapeList = List.of(shapes);

			return this;
		}

		public Config withAgeProperty(IntProperty age) {
			return this;
		}
	}
}
