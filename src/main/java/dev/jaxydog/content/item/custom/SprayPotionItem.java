package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.content.item.CustomPotionItem;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SprayPotionItem extends CustomPotionItem implements Sprayable {

	public static final int MAX_USES = 6;
	public static final float DURATION_MULTIPLIER = 1F / (float) MAX_USES;

	public SprayPotionItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	public Potion getPotion(ItemStack stack) {
		final NbtCompound compound = stack.getNbt();

		if (compound != null && compound.contains(PotionUtil.POTION_KEY, NbtElement.STRING_TYPE)) {
			return Potion.byId(compound.getString(PotionUtil.POTION_KEY));
		} else {
			return Potions.EMPTY;
		}
	}

	public void setPotion(ItemStack stack, @Nullable Potion potion) {
		if (potion == null || potion.equals(Potions.EMPTY)) {
			stack.removeSubNbt(PotionUtil.POTION_KEY);
		} else {
			final Identifier identifier = Registries.POTION.getId(potion);
			final NbtCompound compound = stack.getOrCreateNbt();

			compound.putString(PotionUtil.POTION_KEY, identifier.toString());
		}
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
		final Potion potion = this.getPotion(stack);

		boolean added = false;

		for (final StatusEffectInstance effect : potion.getEffects()) {
			if (effect.getEffectType().isInstant()) {
				effect.getEffectType().applyInstantEffect(player, player, entity, effect.getAmplifier(), 1D);

				added = true;
			} else {
				added |= entity.addStatusEffect(this.shortened(effect), player);
			}
		}

		if (added) {
			this.spray(stack, player.getWorld(), player, 1);

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
