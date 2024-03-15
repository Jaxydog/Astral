package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.AstralItem;
import dev.jaxydog.astral.content.sound.SoundContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A spray bottle item containing water.
 *
 * @author Jaxydog
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public class SprayBottleItem extends AstralItem implements Sprayed {

    /**
     * The maximum amount of sprays contained within the bottle.
     *
     * @since 1.6.0
     */
    public static final int MAX_USES = 48;

    /**
     * The sound played when extinguishing a block.
     *
     * @since 2.0.0
     */
    public static final SoundContext EXTINGUISH_BLOCK_SOUND = new SoundContext(SoundEvents.BLOCK_FIRE_EXTINGUISH,
        SoundCategory.BLOCKS,
        0.5F,
        2.6F,
        0.8F
    );
    /**
     * The sound played when extinguishing an entity.
     *
     * @since 2.0.0
     */
    public static final SoundContext EXTINGUISH_ENTITY_SOUND = new SoundContext(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
        SoundCategory.NEUTRAL,
        0.5F,
        2.6F,
        0.8F
    );
    /**
     * The sound played when wetting a sponge.
     *
     * @since 2.0.0
     */
    public static final SoundContext SPONGE_SQUISH_SOUND = new SoundContext(SoundEvents.BLOCK_SLIME_BLOCK_PLACE,
        SoundCategory.BLOCKS
    );

    /**
     * Stores custom behaviors registered to this spray bottle item.
     *
     * @since 2.0.0
     */
    private final Map<Class<? extends SprayTarget>, List<Behavior<? extends SprayTarget>>> behaviors = new Object2ObjectArrayMap<>();

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code #preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     *
     * @since 2.0.0
     */
    public SprayBottleItem(String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup) {
        super(path, settings, preferredGroup);
    }

    /**
     * Creates a new item using the given settings.
     * <p>
     * This item will be added to the default item group.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     *
     * @since 1.6.0
     */
    public SprayBottleItem(String path, Settings settings) {
        super(path, settings);
    }

    @Override
    public <T extends SprayTarget> void addBehavior(Class<T> type, Behavior<T> behavior) {
        this.behaviors.putIfAbsent(type, new ObjectArrayList<>(1));
        this.behaviors.get(type).add(behavior);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SprayTarget> List<Behavior<T>> getBehaviors(Class<T> type) {
        if (!this.behaviors.containsKey(type)) return List.of();

        return this.behaviors.get(type).stream().map(b -> (Behavior<T>) b).toList();
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

        if (this.isFilled(stack)) return TypedActionResult.pass(stack);

        // Attempt to ray-cast for a water source.
        final BlockHitResult result = raycast(world, player, FluidHandling.SOURCE_ONLY);
        final BlockPos pos = result.getBlockPos();

        if (result.getType() != Type.BLOCK) return TypedActionResult.pass(stack);

        // Attempt to refill using the detected water source.
        if (world.canPlayerModifyAt(player, pos) && world.getFluidState(pos).isIn(FluidTags.WATER)) {
            final int charges = this.getMaxCharges(stack) - this.getCharges(stack);
            final RefillContext context = new RefillContext(world, pos, charges);
            final Source source = new Source(stack, player, player.getPos());

            this.refill(source, context);

            return TypedActionResult.success(stack, world.isClient());
        } else {
            return TypedActionResult.pass(stack);
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (this.isEmpty(stack)) return ActionResult.PASS;
        if (!this.spray(stack, player, player.getPos(), entity, false)) return ActionResult.PASS;

        if (player instanceof final ServerPlayerEntity serverPlayer) {
            Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayer, stack, entity);
        }

        return ActionResult.success(player.getWorld().isClient());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (this.isEmpty(context.getStack())) return ActionResult.PASS;

        final ItemStack stack = context.getStack();
        final PlayerEntity player = context.getPlayer();
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos();
        final Direction side = context.getSide();
        final BlockState oldState = world.getBlockState(pos);

        final Vec3d position;

        if (player == null) {
            // We can safely assume this is from a dispenser, in which case it will always be directly adjacent.
            position = pos.offset(side).toCenterPos();
        } else {
            position = player.getPos();
        }

        if (!this.spray(stack, player, position, world, pos, side, false)) return ActionResult.PASS;

        final BlockState newState = world.getBlockState(pos);

        // Emit game events if the state changed.
        if (!oldState.equals(newState)) {
            final Emitter emitter = player == null ? Emitter.of(newState) : Emitter.of(player, newState);

            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, emitter);
        }

        if (player instanceof final ServerPlayerEntity serverPlayer) {
            Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }

        // Don't bother swinging the player's hand if they don't exist.
        return ActionResult.success(player != null && world.isClient());
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);

        stack.setDamage(stack.getMaxDamage());
    }

    @Override
    public void registerCommon() {
        // Entity extinguishing.
        this.addBehavior(EntityTarget.class, new Behavior<>((source, target) -> target.target().isOnFire(),
            (source, target) -> target.target().extinguishWithSound(),
            2
        ));

        // Block oxidization.
        this.addBehavior(BlockTarget.class, new Behavior<>((source, target) -> {
            final Block block = target.state().getBlock();

            return block instanceof Oxidizable oxidizable && Oxidizable.getIncreasedOxidationBlock(block).isPresent();
        }, (source, target) -> {
            final Block block = target.state().getBlock();
            final Optional<Block> increased = Oxidizable.getIncreasedOxidationBlock(block);

            increased.ifPresent(value -> target.world().setBlockState(target.pos(), value.getDefaultState()));
        }, 1, 100));

        // Farmland moisturization.
        this.addBehavior(BlockTarget.class, new Behavior<>((source, target) -> {
            final BlockState state = target.state();
            final Block block = state.getBlock();

            return block instanceof FarmlandBlock && state.get(FarmlandBlock.MOISTURE) < FarmlandBlock.MAX_MOISTURE;
        }, (source, target) -> {
            final BlockState state = target.state();

            target.world().setBlockState(target.pos(), state.with(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE));
        }, 4));

        // Fire extinguishing.
        this.addBehavior(BlockTarget.class, new Behavior<>((source, target) -> {
            final BlockState state = target.state();
            final Block block = state.getBlock();

            return block instanceof AbstractFireBlock;
        }, (source, target) -> {
            final BlockState state = target.state();

            if (source.actor() == null) {
                target.world().breakBlock(target.pos(), false);
            } else {
                target.world().breakBlock(target.pos(), false, source.actor());
            }

            EXTINGUISH_BLOCK_SOUND.play(target.world(), target.pos(), false);
        }, 2));

        // Campfire extinguishing.
        this.addBehavior(BlockTarget.class, new Behavior<>((source, target) -> {
            final BlockState state = target.state();
            final Block block = state.getBlock();

            return block instanceof CampfireBlock && state.get(CampfireBlock.LIT);
        }, (source, target) -> {
            final BlockState state = target.state();

            target.world().setBlockState(target.pos(), state.with(CampfireBlock.LIT, false));

            EXTINGUISH_BLOCK_SOUND.play(target.world(), target.pos(), false);
        }, 2));

        // Sponge drying.
        this.addBehavior(BlockTarget.class, new Behavior<>((source, target) -> {
            final BlockState state = target.state();

            return state.isOf(Blocks.SPONGE);
        }, (source, target) -> {
            final BlockState state = target.state();

            target.world().setBlockState(target.pos(), Blocks.SPONGE.getDefaultState());

            SPONGE_SQUISH_SOUND.play(target.world(), target.pos(), false);
        }, 4));

        super.registerCommon();

        // Allow refilling using cauldrons.
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(this, (blockState, world, pos, player, hand, stack) -> {
            if (this.isFilled(stack)) return ActionResult.PASS;

            if (!world.isClient()) {
                final int charges = this.getMaxCharges(stack) - this.getCharges(stack);
                final RefillContext context = new RefillContext(world, pos, charges);
                final Source source = new Source(stack, player, player.getPos());

                this.refill(source, context);

                player.incrementStat(Stats.USE_CAULDRON);

                LeveledCauldronBlock.decrementFluidLevel(blockState, world, pos);
            }

            return ActionResult.success(world.isClient());
        });

        // Allow dispensers to use the bottle.
        DispenserBlock.registerBehavior(this, new FallibleItemDispenserBehavior() {

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if (stack.getItem() instanceof final SprayBottleItem item) {
                    this.setSuccess(false);

                    if (item.isEmpty(stack)) return stack;
                } else {
                    return super.dispenseSilently(pointer, stack);
                }

                final ServerWorld world = pointer.getWorld();
                final Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                final BlockPos pos = pointer.getPos().offset(direction);
                final Vec3d position = pointer.getPos().toCenterPos();
                final Direction side = direction.getOpposite();
                final BlockState blockState = world.getBlockState(pos);

                boolean sprayed = false;

                // Spray entities in front of the dispenser.
                final List<Entity> entities = world.getEntitiesByClass(Entity.class,
                    new Box(pos),
                    EntityPredicates.EXCEPT_SPECTATOR
                );

                for (final Entity entity : entities) {
                    sprayed |= item.spray(stack, null, position, entity, true);
                }

                sprayed |= item.spray(stack, null, position, world, pos, side, true);

                if (sprayed) {
                    this.setSuccess(true);

                    final BlockState newState = world.getBlockState(pos);

                    if (!blockState.equals(newState)) {
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, Emitter.of(newState));
                    }

                    SPRAY_SOUND.play(world, position);
                }

                return stack;
            }

        });
    }

}
