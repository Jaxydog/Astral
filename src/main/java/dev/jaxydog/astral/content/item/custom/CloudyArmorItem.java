package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.ColoredArmorItem;
import dev.jaxydog.astral.utility.NbtHelper;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Implements the `Cloudy` interface for colored armor items */
public class CloudyArmorItem extends ColoredArmorItem implements Cloudy, DyeableItem {

	public CloudyArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(rawId, material, type, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getStorminessTooltip(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		if (index == 0) return this.getColor(stack);
		if (index == 1) return this.getStorminessColor(stack);

		return 0xFFFFFF;
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		Cloudy.setStorminess(stack, Cloudy.MIN_VALUE);
		this.setColor(stack, 0xFFFFFF);

		return stack;
	}

	/** Returns the armor's effect strength depending on how many items are equipped */
	protected float getEffectStrength(LivingEntity entity) {
		var strength = 0.0F;

		for (var type : Type.values()) {
			var equipped = entity.getEquippedStack(type.getEquipmentSlot());

			if (equipped.isEmpty() || !(equipped.getItem() instanceof CloudyArmorItem)) continue;

			strength += (1.0F / Type.values().length);
		}

		return strength;
	}

	@Override
	public int getTextureLayers(ItemStack stack) {
		return 2;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		this.updateStorminess(stack, world, entity, 0.375D, 0.125D);

		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!NbtHelper.contains(stack, Cloudy.STORMINESS_KEY)) {
			Cloudy.setStorminess(stack, Cloudy.MIN_VALUE);
		}

		super.onCraft(stack, world, player);
	}
}
