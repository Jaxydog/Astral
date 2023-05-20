package dev.jaxydog.astral_additions.content.item.custom;

import dev.jaxydog.astral_additions.content.item.CustomItem;
import dev.jaxydog.astral_additions.content.item.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/** Custom item extension used to implement the currency system */
public class CurrencyItem extends CustomItem implements Currency {

	/** The NBT key that prevents automatic crafting of currency items */
	public static final String AUTOCRAFT_KEY = "AutoCraft";
	/** The chance of gaining a reward item when autocrafting */
	public static final float REWARD_CHANCE = 0.05f;

	public CurrencyItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	@Override
	public boolean attemptAutoCraft(ItemStack stack, Random random, PlayerEntity player) {
		var optionalThisValue = Currency.Value.from(Currency.getStackId(stack));

		if (optionalThisValue.isEmpty()) return false;

		var thisValue = optionalThisValue.get();
		var optionalNextValue = thisValue.getNext();

		if (optionalNextValue.isEmpty()) return false;

		var nextValue = optionalNextValue.get();
		var inventory = player.getInventory();

		var costForOne = nextValue.getShards() / thisValue.getShards();
		var matchCount = 0;

		for (var slot = 0; slot < inventory.size(); slot += 1) {
			var inventoryStack = inventory.getStack(slot);

			if (!Currency.matchStack(inventoryStack, thisValue, true)) continue;

			matchCount += inventoryStack.getCount();
		}

		if (matchCount < costForOne) return false;

		var totalCrafted = matchCount / costForOne;
		var costForAll = matchCount - (matchCount % costForOne);
		var totalRewards = Currency.getRewardCount(thisValue, totalCrafted, random);

		totalCrafted -= totalRewards;

		for (var remaining = totalCrafted; remaining > 0; remaining -= this.getMaxCount()) {
			var craftedStack = this.getCustomStack(nextValue);

			craftedStack.setCount(Math.min(remaining, this.getMaxCount()));
			inventory.offerOrDrop(craftedStack);
		}

		for (var remaining = totalRewards; remaining > 0; remaining -= this.getMaxCount()) {
			var rewardStack = CustomItems.CURRENCY_REWARD.getRandomStack(random);

			inventory.offerOrDrop(rewardStack);
		}

		var craftingInventory = player.playerScreenHandler.getCraftingInput();

		inventory.remove(s -> Currency.matchStack(s, thisValue, true), costForAll, craftingInventory);

		return true;
	}

	@Override
	public ItemStack getDefaultStack() {
		return this.getCustomStack(Currency.Value.SHARD);
	}

	public ItemStack getCustomStack(int id) {
		var stack = super.getDefaultStack();

		Currency.setAutoCraftable(stack, true);
		Currency.setStackId(stack, id);

		return stack;
	}

	public ItemStack getCustomStack(Currency.Value value) {
		return this.getCustomStack(value.getId());
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		var id = Currency.getStackId(stack);
		var value = Currency.Value.from(id);

		if (value.isPresent()) {
			var suffix = value.get().toString().toLowerCase();

			return super.getTranslationKey(stack) + "." + suffix;
		} else {
			return super.getTranslationKey(stack) + "." + id;
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (entity instanceof PlayerEntity && Currency.isAutoCraftable(stack)) {
			this.attemptAutoCraft(stack, world.getRandom(), (PlayerEntity) entity);
		}
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		super.onCraft(stack, world, player);

		Currency.setAutoCraftable(stack, false);
	}
}
