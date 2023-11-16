package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.color.ColoredArmorItem;
import dev.jaxydog.utility.NbtUtil;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Implements the 'Cloud' interface for colored armor items */
public class CloudArmorItem extends ColoredArmorItem implements Cloud {

	public CloudArmorItem(String rawId, ArmorMaterial material, Type type, Settings settings) {
		super(rawId, material, type, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip,
			TooltipContext context) {
		tooltip.add(this.getStorminessText(stack));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		return index == 0 ? this.getStorminessColor(stack) : 0xFF_FF_FF;
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = super.getDefaultStack();

		this.setStorminess(stack, Cloud.MIN_STORMINESS);

		return stack;
	}

	@Override
	public int getTextureLayers() {
		return 3;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot,
			boolean selected) {
		this.updateStorminess(stack, entity, 1.0D / 40.0D, 1.0D / 80.0D);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!NbtUtil.contains(stack, Cloud.STORMINESS_KEY)) {
			this.setStorminess(stack, this.getStorminess(this.getDefaultStack()));
		}

		super.onCraft(stack, world, player);
	}

}
