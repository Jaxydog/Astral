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

/** Implements the 'DyeableItem' interface for cloud armor items */
public class DyeableCloudArmorItem extends CloudArmorItem implements DyeableItem {

	public DyeableCloudArmorItem(String rawId, ArmorMaterial material, Type type,
			Settings settings) {
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
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot,
			boolean selected) {
		this.updateStorminess(stack, entity, 1.0D / 160.0D, 1.0D / 320.0D);

		final ArrayList<ItemStack> equipped = Lists.newArrayList(entity.getArmorItems());
		final boolean full = equipped.size() == 4;
		final boolean set = equipped.stream().allMatch(s -> s.getItem() instanceof Cloud);
		final double level = equipped.stream().map(this::getStorminess).reduce(0D, (a, b) -> a + b);

		if (entity instanceof LivingEntity living && full && set) {
			final boolean dry = level / 4.0D < 0.5D;
			final StatusEffect type = dry ? StatusEffects.JUMP_BOOST : StatusEffects.SLOWNESS;
			final StatusEffectInstance effect = new StatusEffectInstance(type, 20, 0, false, false);

			living.setStatusEffect(effect, living);
		}
	}

}
