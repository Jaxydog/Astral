package dev.jaxydog.content.item.custom;

import dev.jaxydog.Astral;
import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.content.sound.CustomSoundEvents;
import dev.jaxydog.register.Registered;
import dev.jaxydog.utility.SprayableEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SprayBottleItem extends CustomItem implements Registered.Client {

	public static final TagKey<Block> SPRAYABLE = TagKey.of(Registries.BLOCK.getKey(), Astral.getId("sprayable"));

	public static final int SPRAY_USES = 48;
	public static final int SPRAY_INTERVAL = 8;
	public static final int SPRAY_DURATION = 40;

	public SprayBottleItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling handling) {
		final float toRad = (float) Math.PI / 180F;
		final float pitch = player.getPitch() * toRad;
		final float yaw = player.getYaw() * toRad - (float) Math.PI;

		final float xr = MathHelper.sin(-yaw);
		final float zr = MathHelper.cos(-yaw);
		final float xzr = -MathHelper.cos(-pitch);

		final float x = xr * xzr * 5F;
		final float y = MathHelper.sin(-pitch) * 5F;
		final float z = zr * xzr * 5F;

		final Vec3d start = player.getEyePos();
		final Vec3d end = start.add(x, y, z);

		return world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, handling, player));
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if (this.isEmpty(stack)) {
			final String key = stack.getItem().getTranslationKey(stack) + ".empty";

			tooltip.add(Text.translatable(key).formatted(Formatting.GRAY));
		}

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getStackInHand(hand);

		if (!stack.isDamaged()) {
			return TypedActionResult.pass(stack);
		}

		final BlockHitResult result = raycast(world, player, FluidHandling.SOURCE_ONLY);

		if (result.getType() != Type.BLOCK) {
			return TypedActionResult.pass(stack);
		}

		final BlockPos blockPos = result.getBlockPos();

		if (world.canPlayerModifyAt(player, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
			this.fill(player, world, blockPos, stack);

			return TypedActionResult.success(stack);
		} else {
			return TypedActionResult.pass(stack);
		}
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (!this.isEmpty(stack) && entity instanceof final SprayableEntity sprayable) {
			if (sprayable.astral$canSpray()) {

				this.onSpray(user.getWorld(), stack, user);

				sprayable.astral$setSprayDuration(user, SPRAY_DURATION);

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		} else {
			return super.useOnEntity(stack, user, entity, hand);
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		final World world = context.getWorld();
		final PlayerEntity player = context.getPlayer();
		final ItemStack stack = context.getStack();
		final BlockPos blockPos = context.getBlockPos();

		if (player == null || !world.canPlayerModifyAt(player, blockPos) || this.isEmpty(stack)) {
			return super.useOnBlock(context);
		}

		final BlockState blockState = world.getBlockState(blockPos);

		if (stack.isDamaged() && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
			this.fill(player, world, blockPos, stack);

			return ActionResult.success(world.isClient());
		}
		if (blockState.isIn(SPRAYABLE)) {
			return this.sprayBlock(world, context.getBlockPos(), stack, player);
		} else {
			return super.useOnBlock(context);
		}
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		stack.setDamage(stack.getMaxDamage());

		super.onCraft(stack, world, player);
	}

	protected void fill(PlayerEntity player, World world, BlockPos blockPos, ItemStack stack) {
		final double x = player.getX();
		final double y = player.getY();
		final double z = player.getZ();

		world.playSound(null, x, y, z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1F, 1F);
		world.emitGameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
		stack.setDamage(0);
	}

	protected void onSpray(World world, ItemStack stack, @Nullable PlayerEntity player) {
		if (player == null || !player.isCreative()) {
			stack.damage(1, world.getRandom(), null);
		}

		if (player != null) {
			player.getItemCooldownManager().set(this, SPRAY_INTERVAL);
			player.incrementStat(Stats.USED.getOrCreateStat(this));

			if (!player.isSilent()) {
				final float pitchVariation = (player.getRandom().nextFloat() - 0.5F) * 0.125F;

				player.playSound(CustomSoundEvents.SPRAY_BOTTLE_USE, 1F, 1F + pitchVariation);
			}
		}
	}

	protected void extinguish(World world, BlockPos pos) {
		final Random random = world.getRandom();
		final float pitch = 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f;

		world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, pitch, false);
	}

	protected ActionResult sprayBlock(World world, BlockPos blockPos, ItemStack stack, @Nullable PlayerEntity player) {
		final BlockState blockState = world.getBlockState(blockPos);
		final Block block = blockState.getBlock();

		final AtomicReference<BlockState> changedState = new AtomicReference<>();
		ActionResult result = ActionResult.PASS;

		if (block instanceof final Oxidizable oxidizable) {
			Oxidizable.getIncreasedOxidationBlock(block).ifPresent(oxidized -> {
				changedState.set(oxidized.getDefaultState());

				world.setBlockState(blockPos, changedState.get());
			});

			result = ActionResult.SUCCESS;
		} else if (block instanceof final FarmlandBlock farmland
			&& blockState.get(FarmlandBlock.MOISTURE) < FarmlandBlock.MAX_MOISTURE) {
			changedState.set(blockState.with(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE));
			world.setBlockState(blockPos, changedState.get(), Block.NOTIFY_LISTENERS);

			result = ActionResult.SUCCESS;
		} else if (block instanceof final CampfireBlock campfire && blockState.get(CampfireBlock.LIT)) {

			changedState.set(blockState.with(CampfireBlock.LIT, false));
			world.setBlockState(blockPos, changedState.get());
			this.extinguish(world, blockPos);

			CampfireBlock.extinguish(player, world, blockPos, blockState);

			result = ActionResult.SUCCESS;
		} else if (block instanceof final AbstractFireBlock fire) {
			if (player == null) {
				world.breakBlock(blockPos, false);
			} else {
				world.breakBlock(blockPos, false, player);
			}

			this.extinguish(world, blockPos);

			result = ActionResult.SUCCESS;
		} else if (block instanceof final SpongeBlock sponge) {
			changedState.set(Blocks.WET_SPONGE.getDefaultState());
			world.setBlockState(blockPos, changedState.get(), Block.NOTIFY_LISTENERS);

			result = ActionResult.SUCCESS;
		}

		if (result.isAccepted()) {
			final BlockState state = changedState.get();

			if (state != null) {
				final GameEvent.Emitter emitter;

				if (player == null) {
					emitter = GameEvent.Emitter.of(state);
				} else {
					emitter = GameEvent.Emitter.of(player, state);
				}

				world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, emitter);
			}

			if (player instanceof final ServerPlayerEntity entity) {
				Criteria.ITEM_USED_ON_BLOCK.trigger(entity, blockPos, stack);
			}

			this.onSpray(world, stack, player);
		}

		return result;
	}

	public boolean isEmpty(ItemStack stack) {
		return stack.getItem() instanceof SprayBottleItem && stack.getDamage() >= stack.getMaxDamage();
	}

	public float getEmptyModel(ItemStack stack, World world, LivingEntity entity, int seed) {
		return this.isEmpty(stack) ? 1F : 0F;
	}

	@Override
	public void registerClient() {
		ModelPredicateProviderRegistry.register(this, new Identifier("empty"), this::getEmptyModel);

		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(this, (state, world, pos, player, hand, stack) -> {
			if (!stack.isDamaged()) return ActionResult.PASS;

			if (!world.isClient()) {
				this.fill(player, world, pos, stack);
				player.incrementStat(Stats.USE_CAULDRON);

				LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
			}

			return ActionResult.success(world.isClient());
		});
		DispenserBlock.registerBehavior(this, new FallibleItemDispenserBehavior() {

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				final ServerWorld world = pointer.getWorld();
				final Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				final BlockPos blockPos = pointer.getPos().offset(direction);

				if (stack.getItem() instanceof final SprayBottleItem item) {
					if (!item.isEmpty(stack)) {
						final BlockState state = world.getBlockState(blockPos);

						if (state.isIn(SPRAYABLE) && item.sprayBlock(world, blockPos, stack, null).isAccepted()) {
							item.onSpray(world, stack, null);

							this.setSuccess(true);
						} else {
							this.setSuccess(false);
						}
					} else {
						this.setSuccess(false);
					}

					return stack;
				}

				return super.dispenseSilently(pointer, stack);
			}

		});
	}

}
