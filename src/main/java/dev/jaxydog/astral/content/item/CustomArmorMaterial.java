package dev.jaxydog.astral.content.item;

import net.minecraft.item.ArmorItem.Type;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class CustomArmorMaterial implements ArmorMaterial {

	/** Contains the base durability values for each armor item */
	private static final int[] BASE_DURABILITY = new int[] { 13, 15, 16, 11 };

	// The values below just store each setting for the material
	private final String NAME;
	private int[] durability = new int[] { 1, 1, 1, 1 };
	private int[] protection = new int[] { 0, 0, 0, 0 };
	private int enchantability = 0;
	private SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
	private Ingredient repairIngredient = Ingredient.empty();
	private float toughness = 0.0f;
	private float knockbackResistance = 0.0f;

	public CustomArmorMaterial(String name) {
		this.NAME = name;
	}

	@Override
	public int getDurability(Type type) {
		return this.durability[type.getEquipmentSlot().getEntitySlotId()];
	}

	@Override
	public int getProtection(Type type) {
		return this.protection[type.getEquipmentSlot().getEntitySlotId()];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.equipSound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient;
	}

	@Override
	public String getName() {
		return this.NAME;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}

	public CustomArmorMaterial setDurability(Type type, float durability) {
		var index = type.getEquipmentSlot().getEntitySlotId();
		var modifier = Math.round(CustomArmorMaterial.BASE_DURABILITY[index] * durability);

		this.durability[index] = Math.max(1, modifier);

		return this;
	}

	public CustomArmorMaterial setDurability(Type type, int durability) {
		return this.setDurability(type, (float) durability);
	}

	public CustomArmorMaterial setDurability(float helmet, float chestplate, float leggings, float boots) {
		this.setDurability(Type.HELMET, helmet);
		this.setDurability(Type.CHESTPLATE, chestplate);
		this.setDurability(Type.LEGGINGS, leggings);
		this.setDurability(Type.BOOTS, boots);

		return this;
	}

	public CustomArmorMaterial setDurability(int helmet, int chestplate, int leggings, int boots) {
		return this.setDurability((float) helmet, (float) chestplate, (float) leggings, (float) boots);
	}

	public CustomArmorMaterial setDurability(int durability) {
		return this.setDurability(durability, durability, durability, durability);
	}

	public CustomArmorMaterial setDurability(float durability) {
		return this.setDurability(durability, durability, durability, durability);
	}

	public CustomArmorMaterial setProtectionAmount(Type type, int protection) {
		this.protection[type.getEquipmentSlot().getEntitySlotId()] = protection;

		return this;
	}

	public CustomArmorMaterial setProtectionAmount(int helmet, int chestplate, int leggings, int boots) {
		this.protection = new int[] { boots, leggings, chestplate, helmet };

		return this;
	}

	public CustomArmorMaterial setEnchantability(int enchantability) {
		this.enchantability = enchantability;
		return this;
	}

	public CustomArmorMaterial setEquipSound(SoundEvent equipSound) {
		this.equipSound = equipSound;
		return this;
	}

	public CustomArmorMaterial setRepairIngredient(Ingredient repairIngredient) {
		this.repairIngredient = repairIngredient;
		return this;
	}

	public CustomArmorMaterial setToughness(float toughness) {
		this.toughness = toughness;
		return this;
	}

	public CustomArmorMaterial setKnockbackResistance(float knockbackResistance) {
		this.knockbackResistance = knockbackResistance;
		return this;
	}
}
