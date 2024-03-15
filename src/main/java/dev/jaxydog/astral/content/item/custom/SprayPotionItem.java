package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.content.item.AstralPotionItem;
import dev.jaxydog.astral.content.power.custom.ActionOnSprayPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A spray bottle item containing potion effects.
 *
 * @author Jaxydog
 * @since 1.7.0
 */
public class SprayPotionItem extends AstralPotionItem implements Sprayed {

    /**
     * The maximum amount of sprays contained within the bottle.
     *
     * @since 1.7.0
     */
    public static final int MAX_USES = 3;
    /**
     * The multiplier used to lessen the duration of the stored potion effects.
     *
     * @since 1.7.0
     */
    public static final float DURATION_MULTIPLIER = 1F / (float) MAX_USES;

    /**
     * Stores custom behaviors registered to this spray bottle item.
     *
     * @since 2.0.0
     */
    private final Map<Class<? extends SprayTarget>, List<Behavior<? extends SprayTarget>>> behaviors = new Object2ObjectArrayMap<>();

    /**
     * Creates a new item using the given settings.
     * <p>
     * If the {@code preferredGroup} supplier is {@code null}, this item will not be added to any item groups.
     *
     * @param path The item's identifier path.
     * @param settings The item's settings.
     * @param preferredGroup The item's preferred item group.
     *
     * @since 2.0.0
     */
    public SprayPotionItem(String path, Settings settings, @Nullable Supplier<RegistryKey<ItemGroup>> preferredGroup) {
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
     * @since 2.0.0
     */
    public SprayPotionItem(String path, Settings settings) {
        super(path, settings);
    }

    /**
     * Returns the item stack's color modifier at the given index.
     *
     * @param stack The item stack.
     * @param index The color index.
     *
     * @return The color as an integer.
     *
     * @since 1.7.0
     */
    public int getColor(ItemStack stack, int index) {
        return index == 0 ? PotionUtil.getColor(stack) : 0xFFFFFF;
    }

    /**
     * Returns a copy of the provided status effect instance with its duration shortened.
     *
     * @param instance The status effect.
     *
     * @return A new status effect.
     *
     * @since 1.7.0
     */
    private StatusEffectInstance shortened(StatusEffectInstance instance) {
        return new StatusEffectInstance(instance.getEffectType(),
            Math.round(instance.getDuration() * DURATION_MULTIPLIER),
            instance.getAmplifier(),
            instance.isAmbient(),
            instance.shouldShowParticles(),
            instance.shouldShowIcon(),
            null,
            instance.getFactorCalculationData()
        );
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
        PotionUtil.buildTooltip(stack, tooltip, DURATION_MULTIPLIER);

        tooltip.addAll(this.getLoreTooltips(stack));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 0;
    }

    private int sprayEntity(ItemStack stack, @Nullable PlayerEntity player, LivingEntity entity) {
        int charges = 0;

        final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(player, ActionOnSprayPower.class);

        powers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

        for (final ActionOnSprayPower power : powers) {
            if (!power.canSprayEntity(stack, entity)) continue;

            if (power.onSprayEntity(stack, entity)) {
                charges = Math.max(charges, power.getCharges());
            }
        }

        // Apply every effect in order.
        for (final StatusEffectInstance effect : PotionUtil.getPotionEffects(stack)) {
            if (entity.hasStatusEffect(effect.getEffectType())) continue;

            if (effect.getEffectType().isInstant()) {
                effect.getEffectType().applyInstantEffect(player, player, entity, effect.getAmplifier(), 1D);

                charges = Math.max(charges, 1);
            } else if (entity.addStatusEffect(this.shortened(effect), player)) {
                charges = Math.max(charges, 1);
            }
        }

        return charges;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (this.isEmpty(stack)) return ActionResult.PASS;
        if (player.getItemCooldownManager().isCoolingDown(this)) return ActionResult.PASS;
        if (!this.spray(stack, player, player.getPos(), entity, true)) return ActionResult.PASS;

        if (this.isEmpty(stack)) {
            player.getInventory().removeOne(stack);
            player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
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
    public void register() {
        this.addBehavior(EntityTarget.class,
            new Behavior<>((source, target) -> target.target() instanceof LivingEntity, (source, target) -> {
                for (final StatusEffectInstance effect : PotionUtil.getPotionEffects(source.stack())) {
                    final LivingEntity entity = (LivingEntity) target.target();

                    if (entity.hasStatusEffect(effect.getEffectType())) continue;

                    if (effect.getEffectType().isInstant()) {
                        final LivingEntity actor = source.actor();
                        final int amplifier = effect.getAmplifier();

                        effect.getEffectType().applyInstantEffect(actor, actor, entity, amplifier, 1D);
                    } else {
                        entity.addStatusEffect(this.shortened(effect), source.actor());
                    }
                }
            }, 1)
        );

        super.register();

        BrewingRecipeRegistry.registerItemRecipe(Items.POTION, AstralItems.CLOUDY_MANE, this);

        DispenserBlock.registerBehavior(this, new FallibleItemDispenserBehavior() {

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if (stack.getItem() instanceof final SprayPotionItem item) {
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

                boolean sprayed = false;

                // Spray entities in front of the dispenser.
                final List<Entity> entities = world.getEntitiesByClass(Entity.class,
                    new Box(pos),
                    EntityPredicates.EXCEPT_SPECTATOR
                );

                for (final Entity entity : entities) {
                    sprayed |= item.spray(stack, null, position, entity, true);

                    if (stack.isEmpty()) break;
                }

                final BlockState oldState = world.getBlockState(pos);

                sprayed |= item.spray(stack, null, position, world, pos, side, true);

                if (!sprayed) return stack;

                final BlockState newState = world.getBlockState(pos);

                if (!oldState.equals(newState)) {
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, Emitter.of(newState));
                }

                SPRAY_SOUND.play(world, position);

                this.setSuccess(true);

                if (stack.getDamage() >= stack.getMaxDamage()) {
                    return Items.GLASS_BOTTLE.getDefaultStack();
                } else {
                    return stack;
                }
            }

        });
    }

    @Override
    public void registerClient() {
        ColorProviderRegistry.ITEM.register(this::getColor, this);
    }

}
