package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.content.item.CustomPotionItem;
import dev.jaxydog.content.power.custom.ActionOnSprayPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Comparator;
import java.util.List;

public class SprayPotionItem extends CustomPotionItem implements Sprayable {

	public static final int MAX_USES = 6;
	public static final float DURATION_MULTIPLIER = 1F / (float) MAX_USES;

	public SprayPotionItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	public int getColor(ItemStack stack, int index) {
		return index == 0 ? PotionUtil.getColor(stack) : 0xFFFFFF;
	}

	private StatusEffectInstance shortened(StatusEffectInstance instance) {
		return new StatusEffectInstance(
			instance.getEffectType(),
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
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		PotionUtil.buildTooltip(stack, tooltip, DURATION_MULTIPLIER);
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

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
		int charges = 0;
		boolean added = false;

		final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(player, ActionOnSprayPower.class);

		powers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

		for (final ActionOnSprayPower power : powers) {
			if (!power.canSprayEntity(stack, entity)) continue;

			if (power.onSprayEntity(stack, entity)) {
				charges = Math.max(charges, power.getCharges());
			}
		}

		for (final StatusEffectInstance effect : PotionUtil.getPotionEffects(stack)) {
			if (effect.getEffectType().isInstant()) {
				effect.getEffectType().applyInstantEffect(player, player, entity, effect.getAmplifier(), 1D);

				added = true;
			} else {
				added |= entity.addStatusEffect(this.shortened(effect), player);
			}
		}

		if (added) {
			this.spray(stack, player.getWorld(), player, Math.max(1, charges));

			if (this.isEmptied(stack)) {
				player.getInventory().removeOne(stack);
				player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
			}

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
		final List<ActionOnSprayPower> powers = PowerHolderComponent.getPowers(player, ActionOnSprayPower.class);

		int charges = 0;

		powers.sort(Comparator.comparingInt(ActionOnSprayPower::getPriority).reversed());

		for (final ActionOnSprayPower power : powers) {
			if (!power.canSprayBlock(stack, world, blockPos, side)) continue;

			if (power.onSprayBlock(stack, world, blockPos, side)) {
				charges = Math.max(charges, power.getCharges());
			}
		}

		if (charges > 0) {
			final BlockState newState = world.getBlockState(blockPos);

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
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public void register() {
		super.register();

		BrewingRecipeRegistry.registerPotionType(this);
		BrewingRecipeRegistry.registerItemRecipe(Items.POTION, CustomItems.CLOUDY_MANE, this);
	}

	@Override
	public void registerClient() {
		ColorProviderRegistry.ITEM.register(this::getColor, this);
	}

}
