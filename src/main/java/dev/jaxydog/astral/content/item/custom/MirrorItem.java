package dev.jaxydog.astral.content.item.custom;

import dev.jaxydog.astral.content.item.CustomItem;
import dev.jaxydog.astral.utility.NbtUtil;
import dev.jaxydog.astral.utility.register.Registerable;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/** Implements the fluxling's mirror */
public class MirrorItem extends CustomItem implements Registerable.Client {

	public static final String BROKEN_KEY = "Broken";

	public MirrorItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Returns the item's model predicate value */
	protected float getModel(ItemStack stack, World world, LivingEntity entity, int seed) {
		return this.isBroken(stack) ? 1.0F : 0.0F;
	}

	/** Returns whether the mirror item is broken */
	public boolean isBroken(ItemStack stack) {
		return NbtUtil.getBoolean(stack, MirrorItem.BROKEN_KEY);
	}

	/** Sets whether the provided stack is broken */
	public void setBroken(ItemStack stack, boolean broken) {
		stack.getOrCreateNbt().putBoolean(MirrorItem.BROKEN_KEY, broken);
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		this.setBroken(stack, false);

		return stack;
	}

	@Override
	public void registerClient() {
		Client.super.registerClient();
		ModelPredicateProviderRegistry.register(this, new Identifier("broken"), this::getModel);
	}
}
