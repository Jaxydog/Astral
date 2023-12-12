package dev.jaxydog.content.item.custom;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import com.google.common.collect.Multimap;
import dev.jaxydog.content.item.Customized;
import dev.jaxydog.utility.RegisterableMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Equipment;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceholderMimicItem extends PlaceholderItem {

	private final Supplier<Item> ITEM;

	public PlaceholderMimicItem(String rawId, Settings settings, Supplier<Item> item) {
		super(rawId, settings);

		this.ITEM = item;
	}

	public Item getItem() {
		return this.ITEM.get();
	}

	public Item getItem(ItemStack stack) {
		return this.getItem();
	}

	@Override
	public EquipmentSlot getSlotType() {
		if (this.getItem() instanceof final Equipment equipment) {
			return equipment.getSlotType();
		} else {
			return super.getSlotType();
		}
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		this.getItem(stack).usageTick(world, user, stack, remainingUseTicks);
	}

	@Override
	public void onItemEntityDestroyed(ItemEntity entity) {
		this.getItem(entity.getStack()).onItemEntityDestroyed(entity);
	}

	@Override
	public void postProcessNbt(NbtCompound nbt) {
		this.getItem().postProcessNbt(nbt);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return this.getItem(miner.getActiveItem()).canMine(state, world, pos, miner);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return this.getItem(context.getStack()).useOnBlock(context);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return this.getItem(stack).getMiningSpeedMultiplier(stack, state);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.getItem(user.getStackInHand(hand)).use(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		return this.getItem(stack).finishUsing(stack, world, user);
	}

	@Override
	public boolean isDamageable() {
		return this.getItem().isDamageable();
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return this.getItem(stack).isItemBarVisible(stack);
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return this.getItem(stack).getItemBarStep(stack);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return this.getItem(stack).getItemBarColor(stack);
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType,
			PlayerEntity player) {
		return this.getItem(stack).onStackClicked(stack, slot, clickType, player);
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType,
			PlayerEntity player, StackReference cursorStackReference) {
		return this.getItem(stack).onClicked(stack, otherStack, slot, clickType, player,
				cursorStackReference);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return this.getItem(stack).postHit(stack, target, attacker);
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos,
			LivingEntity miner) {
		return this.getItem(stack).postMine(stack, world, state, pos, miner);
	}

	@Override
	public boolean isSuitableFor(BlockState state) {
		return this.getItem().isSuitableFor(state);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity,
			Hand hand) {
		return this.getItem(stack).useOnEntity(stack, user, entity, hand);
	}

	@Override
	public String toString() {
		return this.getItem().toString();
	}

	@Override
	public boolean isNbtSynced() {
		return this.getItem().isNbtSynced();
	}

	@Override
	public boolean hasRecipeRemainder() {
		return this.getItem().hasRecipeRemainder();
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot,
			boolean selected) {
		this.getItem(stack).inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		this.getItem(stack).onCraft(stack, world, player);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return this.getItem(stack).getUseAction(stack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return this.getItem(stack).getMaxUseTime(stack);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user,
			int remainingUseTicks) {
		this.getItem(stack).onStoppedUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip,
			TooltipContext context) {
		this.getItem(stack).appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return this.getItem(stack).getTooltipData(stack);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return this.getItem(stack).hasGlint(stack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return this.getItem(stack).getRarity(stack);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return this.getItem(stack).isEnchantable(stack);
	}

	@Override
	public int getEnchantability() {
		return this.getItem().getEnchantability();
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.getItem(stack).canRepair(stack, ingredient);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(
			EquipmentSlot slot) {
		return this.getItem().getAttributeModifiers(slot);
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack) {
		return this.getItem(stack).isUsedOnRelease(stack);
	}

	@Override
	public ItemStack getDefaultStack() {
		final ItemStack stack = this.getItem().getDefaultStack();

		stack.astral$setItem(this);

		return stack;
	}

	@Override
	public boolean isFood() {
		return this.getItem().isFood();
	}

	@Override
	public FoodComponent getFoodComponent() {
		return this.getItem().getFoodComponent();
	}

	@Override
	public SoundEvent getDrinkSound() {
		return this.getItem().getDrinkSound();
	}

	@Override
	public SoundEvent getEatSound() {
		return this.getItem().getEatSound();
	}

	@Override
	public boolean isFireproof() {
		return this.getItem().isFireproof();
	}

	@Override
	public boolean damage(DamageSource source) {
		return this.getItem().damage(source);
	}

	@Override
	public boolean canBeNested() {
		return this.getItem().canBeNested();
	}

	@Override
	public FeatureSet getRequiredFeatures() {
		return this.getItem().getRequiredFeatures();
	}

	@Override
	public boolean isEnabled(FeatureSet enabledFeatures) {
		return this.getItem().isEnabled(enabledFeatures);
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack,
			ItemStack newStack) {
		return this.getItem(oldStack).allowNbtUpdateAnimation(player, hand, oldStack, newStack);
	}

	@Override
	public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack,
			ItemStack newStack) {
		return this.getItem(oldStack).allowContinuingBlockBreaking(player, oldStack, newStack);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack,
			EquipmentSlot slot) {
		return this.getItem(stack).getAttributeModifiers(stack, slot);
	}

	@Override
	public boolean isSuitableFor(ItemStack stack, BlockState state) {
		return this.getItem(stack).isSuitableFor(stack, state);
	}

	@Override
	public ItemStack getRecipeRemainder(ItemStack stack) {
		return this.getItem(stack).getRecipeRemainder(stack);
	}

	@Override
	public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user,
			Hand hand) {
		if (this.getItem(user.getStackInHand(hand)) instanceof final Equipment equipment) {
			return equipment.equipAndSwap(item, world, user, hand);
		} else {
			return super.equipAndSwap(item, world, user, hand);
		}
	}

	@Override
	public SoundEvent getEquipSound() {
		if (this.getItem() instanceof final Equipment equipment) {
			return equipment.getEquipSound();
		} else {
			return super.getEquipSound();
		}
	}

	@Override
	public int getCustomModelData(ItemStack stack) {
		if (this.getItem(stack) instanceof final Customized customized) {
			return customized.getCustomModelData(stack);
		} else {
			return super.getCustomModelData(stack);
		}
	}

	@Override
	public void setCustomModelData(ItemStack stack, int data) {
		if (this.getItem(stack) instanceof final Customized customized) {
			customized.setCustomModelData(stack, data);
		} else {
			super.setCustomModelData(stack, data);
		}
	}

	public static class Group extends RegisterableMap<Item, PlaceholderMimicItem> {

		private final Set<Item> ITEMS;

		public Group(String rawId, Settings settings, Item... items) {
			super(rawId, (id, item) -> new PlaceholderMimicItem(id, settings, () -> item));

			this.ITEMS = Set.of(items);
		}

		@Override
		public Set<Item> keys() {
			return this.ITEMS;
		}

		@Override
		public String getRawId(Item item) {
			final String id = Registries.ITEM.getId(item).getPath();

			return String.format("%s_%s", super.getRawId(), id);
		}

		@Override
		protected int compareKeys(Item a, Item b) {
			return Integer.compare(Item.getRawId(a), Item.getRawId(b));
		}

	}

}
