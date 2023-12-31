package dev.jaxydog.content.item.custom;

import dev.jaxydog.content.item.CustomItem;
import dev.jaxydog.register.Registered;
import dev.jaxydog.utility.NbtUtil;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/** Implements the fluxling's mirror item */
public class MirrorItem extends CustomItem implements Registered.Client {

	/** The NBT key that determines whether a mirror is considered to be broken */
	public static final String BROKEN_KEY = "Broken";

	public MirrorItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		final ItemStack stack = super.getDefaultStack();

		this.setBroken(stack, false);

		return stack;
	}

	/** Sets whether the provided item stack is broken */
	public void setBroken(ItemStack stack, boolean broken) {
		stack.getOrCreateNbt().putBoolean(BROKEN_KEY, broken);
	}

	@Override
	public void registerClient() {
		ModelPredicateProviderRegistry.register(this, new Identifier("broken"), this::getBrokenModel);
	}

	/** Returns the mirror item's model predicate value */
	public float getBrokenModel(ItemStack stack, World world, LivingEntity entity, int seed) {
		return this.isBroken(stack) ? 1F : 0F;
	}

	/** Returns whether the mirror is broken */
	public boolean isBroken(ItemStack stack) {
		return NbtUtil.getBoolean(stack, BROKEN_KEY);
	}

}
