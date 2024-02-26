package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.AstralItem;
import dev.jaxydog.astral.content.power.custom.ActionOnSprayPower;
import dev.jaxydog.astral.content.power.custom.ActionWhenSprayedPower;
import dev.jaxydog.astral.utility.injected.SprayableEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
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
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SprayBottleItem extends AstralItem implements Sprayable {

    public static final int MAX_USES = 48;
    public static final int SPRAY_DURATION = 40;

    @SuppressWarnings("unused")
    public SprayBottleItem(String idPath, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> group) {
        super(idPath, settings, group);
    }

    public SprayBottleItem(String idPath, Settings settings) {
        super(idPath, settings);
    }

    protected void playExtinguishSound(World world, BlockPos pos) {
        final Random random = world.getRandom();
        final float pitch = 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F;

        world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, pitch, false);
    }

    protected int sprayEntity(ItemStack stack, @Nullable PlayerEntity player, LivingEntity entity) {
        int charges = 0;

        if (this.isEmptied(stack)) return charges;

        // Iterate through all on spray powers and apply their effects if possible.
        final List<ActionOnSprayPower> playerPowers = PowerHolderComponent.getPowers(player, ActionOnSprayPower.class);

        playerPowers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

        for (final ActionOnSprayPower power : playerPowers) {
            if (!power.canSprayEntity(stack, entity)) continue;

            if (power.onSprayEntity(stack, entity)) {
                charges = Math.max(charges, power.getCharges());
            }
        }

        // Iterate through all when sprayed powers and apply their effects if possible.
        final List<ActionWhenSprayedPower> entityPowers = PowerHolderComponent.getPowers(entity,
            ActionWhenSprayedPower.class
        );

        entityPowers.sort(Comparator.comparingInt(ActionWhenSprayedPower::getPriority).reversed());

        for (final ActionWhenSprayedPower power : entityPowers) {
            if (!power.canBeSprayed(player, stack)) continue;

            if (power.onSprayed(player, stack)) {
                charges = Math.max(charges, power.getCharges());
            }
        }

        if (entity.isOnFire()) {
            entity.extinguishWithSound();

            charges = Math.max(charges, 2);
        }

        if (!entity.getWorld().isClient()
            && entity instanceof final SprayableEntity sprayable
            && sprayable.astral$canSpray()) {
            sprayable.astral$setSprayed(player, SPRAY_DURATION, true);

            charges = Math.max(charges, sprayable.astral$getSprayCharges());
        }

        return charges;
    }

    protected int sprayBlock(
        ItemStack stack, @Nullable PlayerEntity player, World world, BlockPos blockPos, Direction side
    ) {
        int charges = 0;

        if ((player != null && !world.canPlayerModifyAt(player, blockPos)) || this.isEmptied(stack)) {
            return charges;
        }

        // Refill if possible.
        final BlockState blockState = world.getBlockState(blockPos);

        if (stack.isDamaged() && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            this.fill(stack, world, blockPos, player, this.getMaxDamage());

            return charges;
        }

        // Iterate through all spray powers and apply their effects if possible.
        final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(player, ActionOnSprayPower.class);

        powers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

        for (final ActionOnSprayPower power : powers) {
            if (!power.canSprayBlock(stack, world, blockPos)) continue;

            if (power.onSprayBlock(stack, world, blockPos, side)) {
                charges = Math.max(charges, power.getCharges());
            }
        }

        final Block block = blockState.getBlock();

        if (block instanceof Oxidizable) {
            final Optional<Block> increased = Oxidizable.getIncreasedOxidationBlock(block);

            if (increased.isPresent()) {
                world.setBlockState(blockPos, increased.get().getDefaultState());

                charges = Math.max(charges, 1);
            }
        }

        if (block instanceof FarmlandBlock) {
            final int moisture = blockState.get(FarmlandBlock.MOISTURE);

            if (moisture < FarmlandBlock.MAX_MOISTURE) {
                world.setBlockState(blockPos, blockState.with(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE));

                charges = Math.max(charges, 4);
            }
        }

        if (block instanceof CampfireBlock) {
            final boolean lit = blockState.get(CampfireBlock.LIT);

            if (lit) {
                world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, false));
                this.playExtinguishSound(world, blockPos);

                charges = Math.max(charges, 2);
            }
        }

        if (block instanceof AbstractFireBlock) {
            if (player == null) {
                world.breakBlock(blockPos, false);
            } else {
                world.breakBlock(blockPos, false, player);
            }

            this.playExtinguishSound(world, blockPos);

            charges = Math.max(charges, 2);
        }

        if (blockState.isOf(Blocks.SPONGE)) {
            world.setBlockState(blockPos, Blocks.WET_SPONGE.getDefaultState());

            charges = Math.max(charges, 4);
        }

        return charges;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (this.isEmptied(stack)) {
            final String key = stack.getItem().getTranslationKey(stack) + ".empty";

            tooltip.add(Text.translatable(key).formatted(Formatting.GRAY));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        final ItemStack stack = player.getStackInHand(hand);

        if (!stack.isDamaged()) return TypedActionResult.pass(stack);

        final BlockHitResult result = raycast(world, player, FluidHandling.SOURCE_ONLY);

        if (result.getType() != Type.BLOCK) return TypedActionResult.pass(stack);

        final BlockPos blockPos = result.getBlockPos();

        // Attempt to refill using a water source.
        if (world.canPlayerModifyAt(player, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            this.fill(stack, world, blockPos, player, this.getMaxDamage());

            return TypedActionResult.success(stack, world.isClient());
        } else {
            return TypedActionResult.pass(stack);
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        final int charges = this.sprayEntity(stack, player, entity);

        if (charges > 0) {
            this.spray(stack, player.getWorld(), player, charges);

            return ActionResult.success(player.getWorld().isClient());
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        final ItemStack stack = context.getStack();
        final PlayerEntity player = context.getPlayer();
        final World world = context.getWorld();
        final BlockPos blockPos = context.getBlockPos();
        final Direction side = context.getSide();
        final BlockState oldState = world.getBlockState(blockPos);

        final int charges = this.sprayBlock(stack, player, world, blockPos, side);

        if (charges <= 0) return ActionResult.PASS;

        final BlockState newState = world.getBlockState(blockPos);

        // Emit game events if the state changed.
        if (!oldState.equals(newState)) {
            final GameEvent.Emitter emitter;

            if (player == null) {
                emitter = GameEvent.Emitter.of(newState);
            } else {
                emitter = GameEvent.Emitter.of(player, newState);
            }

            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, emitter);
        }

        if (player instanceof final ServerPlayerEntity serverPlayer) {
            Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, stack);
        }

        this.spray(stack, world, player, charges);

        return ActionResult.success(player.getWorld().isClient());
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);

        stack.setDamage(stack.getMaxDamage());
    }

    @Override
    public void register() {
        super.register();

        // Allow refill using cauldrons.
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(this, (blockState, world, blockPos, player, hand, stack) -> {
            if (!stack.isDamaged()) return ActionResult.PASS;

            if (!world.isClient()) {
                this.fill(stack, world, blockPos, player, stack.getMaxDamage());

                player.incrementStat(Stats.USE_CAULDRON);

                LeveledCauldronBlock.decrementFluidLevel(blockState, world, blockPos);
            }

            return ActionResult.success(world.isClient());
        });
        // Allow dispensers to use the bottle.
        DispenserBlock.registerBehavior(this, new FallibleItemDispenserBehavior() {

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if (stack.getItem() instanceof final SprayBottleItem item) {
                    this.setSuccess(false);

                    if (item.isEmptied(stack)) return stack;
                } else {
                    return super.dispenseSilently(pointer, stack);
                }

                final ServerWorld world = pointer.getWorld();
                final Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                final BlockPos blockPos = pointer.getPos().offset(direction);

                int charges = 0;

                // Spray entities in front of the dispenser.
                final List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
                    new Box(blockPos),
                    EntityPredicates.EXCEPT_SPECTATOR
                );

                for (final LivingEntity entity : entities) {
                    charges = Math.max(charges, item.sprayEntity(stack, null, entity));
                }

                final BlockState blockState = world.getBlockState(blockPos);
                final Direction side = direction.getOpposite();

                charges = Math.max(charges, item.sprayBlock(stack, null, world, blockPos, side));

                // Only spray if charges are > 0.
                if (charges > 0) {
                    item.spray(stack, world, null, charges);

                    this.setSuccess(true);

                    final BlockState newState = world.getBlockState(blockPos);

                    if (!blockState.equals(newState)) {
                        final GameEvent.Emitter emitter = GameEvent.Emitter.of(newState);

                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, emitter);
                    }
                }

                return stack;
            }

        });
    }

    @Override
    public void registerClient() {
        ModelPredicateProviderRegistry.register(this, new Identifier("empty"), this::getEmptyModel);
    }

}
