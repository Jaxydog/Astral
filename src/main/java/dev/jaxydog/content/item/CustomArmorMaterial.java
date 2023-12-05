package dev.jaxydog.content.item;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorItem.Type;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/** Implements the `ArmorMaterial` interface as a buildable class */
public final class CustomArmorMaterial implements ArmorMaterial {

	// Stores the material's properties
	private final Map<Type, Integer> DURABILITY;
	private final Map<Type, Integer> PROTECTION;
	private final int ENCHANTABILITY;
	private final SoundEvent EQUIP_SOUND;
	private final Ingredient REPAIR_INGREDIENT;
	private final String NAME;
	private final float TOUGHNESS;
	private final float KNOCKBACK_RESISTANCE;

	private CustomArmorMaterial(Map<Type, Integer> durability, Map<Type, Integer> protection,
			int enchantability, SoundEvent equipSound, Ingredient repairIngredient, String name,
			float toughness, float knockbackResistance) {
		this.DURABILITY = durability;
		this.PROTECTION = protection;
		this.ENCHANTABILITY = enchantability;
		this.EQUIP_SOUND = equipSound;
		this.REPAIR_INGREDIENT = repairIngredient;
		this.NAME = name;
		this.TOUGHNESS = toughness;
		this.KNOCKBACK_RESISTANCE = knockbackResistance;
	}

	public static final Builder builder(String name) {
		return new Builder(name);
	}

	@Override
	public int getDurability(Type type) {
		return this.DURABILITY.get(type);
	}

	@Override
	public int getProtection(Type type) {
		return this.PROTECTION.get(type);
	}

	@Override
	public int getEnchantability() {
		return this.ENCHANTABILITY;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.EQUIP_SOUND;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.REPAIR_INGREDIENT;
	}

	@Override
	public String getName() {
		return this.NAME;
	}

	@Override
	public float getToughness() {
		return this.TOUGHNESS;
	}

	@Override
	public float getKnockbackResistance() {
		return this.KNOCKBACK_RESISTANCE;
	}

	/** Builder class for custom armor materials */
	public static final class Builder {

		// Stores the material's properties
		private final String NAME;
		private HashMap<Type, Integer> durability = new HashMap<>(Type.values().length);
		private HashMap<Type, Integer> protection = new HashMap<>(Type.values().length);
		private int enchantability = 0;
		private SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
		private Ingredient repairIngredient = Ingredient.empty();
		private float toughness = 0F;
		private float knockbackResistance = 0F;

		private Builder(String name) {
			this.NAME = name;
		}

		public final Builder setDurability(ArmorItem.Type type, int durability) {
			this.durability.put(type, Math.max(1, durability));

			return this;
		}

		public final Builder setDurability(int helmet, int chestplate, int leggings, int boots) {
			return this.setDurability(Type.HELMET, helmet)
					.setDurability(Type.CHESTPLATE, chestplate)
					.setDurability(Type.LEGGINGS, leggings).setDurability(Type.BOOTS, boots);
		}

		public final Builder setDurability(int durability) {
			return this.setDurability(durability, durability, durability, durability);
		}

		public final Builder setProtectionAmount(ArmorItem.Type type, int protection) {
			this.protection.put(type, protection);

			return this;
		}

		public final Builder setProtectionAmount(int helmet, int chestplate, int leggings,
				int boots) {
			return this.setProtectionAmount(Type.HELMET, helmet)
					.setProtectionAmount(Type.CHESTPLATE, chestplate)
					.setProtectionAmount(Type.LEGGINGS, leggings)
					.setProtectionAmount(Type.BOOTS, boots);
		}

		public final Builder setProtectionAmount(int protection) {
			return this.setProtectionAmount(protection, protection, protection, protection);
		}

		public final Builder setEnchantability(int enchantability) {
			this.enchantability = enchantability;

			return this;
		}

		public final Builder setEquipSound(SoundEvent equipSound) {
			this.equipSound = equipSound;

			return this;
		}

		public final Builder setRepairIngredient(Ingredient repairIngredient) {
			this.repairIngredient = repairIngredient;

			return this;
		}

		public final Builder setToughness(float toughness) {
			this.toughness = toughness;

			return this;
		}

		public final Builder setKnockbackResistance(float knockbackResistance) {
			this.knockbackResistance = knockbackResistance;

			return this;
		}

		public final CustomArmorMaterial build() {
			return new CustomArmorMaterial(this.durability, this.protection, this.enchantability,
					this.equipSound, this.repairIngredient, this.NAME, this.toughness,
					this.knockbackResistance);
		}

	}

}
