package dev.jaxydog.content.item.custom;

import java.util.ArrayList;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DyeableCloudyArmorItem extends CloudyArmorItem implements DyeableItem {

	private static final double INCREASE_DELTA = 1D / 160D;
	private static final double DECREASE_DELTA = 1D / 320D;

	public DyeableCloudyArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(rawId, material, type, settings);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		return switch (index) {
			case 0 -> this.getColor(stack);
			case 1 -> this.getStorminessColor(stack);
			default -> 0xFF_FF_FF;
		};
	}

	@Override
	public ItemStack getDefaultStack() {
		final ItemStack stack = super.getDefaultStack();

		this.setColor(stack, DyeableItem.DEFAULT_COLOR);

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		this.updateStorminess(stack, entity, INCREASE_DELTA, DECREASE_DELTA);

		final ArrayList<ItemStack> armor = Lists.newArrayList(entity.getArmorItems());
		final boolean cloudy = armor.stream().allMatch(s -> s.getItem() instanceof DyeableCloudyArmorItem);

		if (armor.size() == 4 && cloudy && entity instanceof final LivingEntity living) {
			final double level = armor.stream().map(this::getStorminess).reduce(0D, (a, b) -> a + b) / 4D;
			final StatusEffect type = level < 0.5D ? StatusEffects.JUMP_BOOST : StatusEffects.SLOWNESS;

			living.addStatusEffect(new StatusEffectInstance(type, 20, 0, false, false));
		}

		super.inventoryTick(stack, world, entity, slot, selected);
	}

}
