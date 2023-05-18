package dev.jaxydog.astral_additions.content.item.custom;

import dev.jaxydog.astral_additions.content.item.CustomItem;
import dev.jaxydog.astral_additions.content.item.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** Used to implement the currency system */
public class CurrencyItem extends CustomItem {

	/** The currency type NBT key */
	public static final String CURRENCY_TYPE_KEY = "CurrencyType";
	/** The NBT key that prevents automatic crafting of currency items */
	public static final String NO_AUTOCRAFT_KEY = "NoAutocraft";
	/** The chance of gaining a reward item when autocrafting */
	public static final float REWARD_CHANCE = 0.01f;

	public CurrencyItem(String rawId, Settings settings) {
		super(rawId, settings);
	}

	/** Extracts and parses the item stack's currency type from its NBT data */
	public static final CurrencyType getCurrencyType(ItemStack stack) {
		var nbt = stack.getNbt();

		if (nbt == null) return CurrencyType.SHARD;

		var id = nbt.getByte(CurrencyItem.CURRENCY_TYPE_KEY);

		return CurrencyType.fromId(id);
	}

	/** Sets the given item stack's currency type via NBT data */
	public static final void setCurrencyType(ItemStack stack, CurrencyType type) {
		var nbt = stack.getOrCreateNbt();
		nbt.putByte(CurrencyItem.CURRENCY_TYPE_KEY, type.getId());
		nbt.putInt("CustomModelData", type.getId());
	}

	/** Returns `true` if the given item stack has autocrafting disabled */
	public static final boolean isAutocraftDisabled(ItemStack stack) {
		var nbt = stack.getNbt();

		if (nbt == null) return false;

		return nbt.getBoolean(CurrencyItem.NO_AUTOCRAFT_KEY);
	}

	/** Sets the given item stack's autocraft to be enabled or disabled */
	public static final void setAutocraftDisabled(ItemStack stack, boolean disabled) {
		stack.getOrCreateNbt().putBoolean(CurrencyItem.NO_AUTOCRAFT_KEY, disabled);
	}

	@Override
	public ItemStack getDefaultStack() {
		var stack = super.getDefaultStack();

		CurrencyItem.setCurrencyType(stack, CurrencyType.SHARD);
		CurrencyItem.setAutocraftDisabled(stack, false);

		return stack;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		var type = CurrencyItem.getCurrencyType(stack);
		var suffix = type.toString().toLowerCase();

		return super.getTranslationKey(stack) + "." + suffix;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (!(entity instanceof PlayerEntity)) return;
		if (CurrencyItem.isAutocraftDisabled(stack)) return;

		var type = CurrencyItem.getCurrencyType(stack);
		var next = type.getNext();

		if (next == null) return;

		var requires = next.getValue() / type.getValue();
		var count = stack.getCount();

		if (count < requires) return;

		var total = count / requires;

		if (next == CurrencyType.SKULL && world.getRandom().nextFloat() <= CurrencyItem.REWARD_CHANCE) {
			var reward = CustomItems.CURRENCY_REWARD.getRandomStack(world.getRandom());

			if (!((PlayerEntity) entity).giveItemStack(reward)) entity.dropStack(reward);

			total -= 1;
		}

		var player = ((PlayerEntity) entity);
		var crafted = this.getDefaultStack();

		CurrencyItem.setCurrencyType(crafted, next);
		crafted.setCount(total);

		if (!player.giveItemStack(crafted)) entity.dropStack(crafted);

		if (count % requires == 0) {
			player.getInventory().removeStack(slot);
		} else {
			stack.setCount(count % requires);
		}
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		super.onCraft(stack, world, player);

		CurrencyItem.setAutocraftDisabled(stack, true);
	}

	/** Represents the type of currency that an item stack represents; should be stored in NBT data */
	public static enum CurrencyType {
		SHARD((byte) 0x00, 1),
		HALF((byte) 0x01, 4),
		BONE((byte) 0x02, 8),
		SKULL((byte) 0x03, 80),
		BIG_SKULL((byte) 0x04, 160);

		/** The type's internal identifier */
		private final byte ID;
		/** The type's monetary value compared to shards */
		private final int VALUE;

		private CurrencyType(byte id, int value) {
			this.ID = id;
			this.VALUE = value;
		}

		/** Returns a currency type with a matching identifier, returning `SHARD` if it does not exist */
		public static CurrencyType fromId(byte id) {
			switch (id) {
				case 0x00:
					return CurrencyType.SHARD;
				case 0x01:
					return CurrencyType.HALF;
				case 0x02:
					return CurrencyType.BONE;
				case 0x03:
					return CurrencyType.SKULL;
				case 0x04:
					return CurrencyType.BIG_SKULL;
				default:
					return CurrencyType.SHARD;
			}
		}

		/** Returns the type's internal identifier */
		public byte getId() {
			return this.ID;
		}

		/** Returns the type's monetary value compared to shards */
		public int getValue() {
			return this.VALUE;
		}

		/** Returns the type's next possible type upgrade */
		public @Nullable CurrencyType getNext() {
			switch (this) {
				case SHARD:
					return CurrencyType.HALF;
				case HALF:
					return CurrencyType.BONE;
				case BONE:
					return CurrencyType.SKULL;
				case SKULL:
					return CurrencyType.BIG_SKULL;
				default:
					return null;
			}
		}
	}
}
