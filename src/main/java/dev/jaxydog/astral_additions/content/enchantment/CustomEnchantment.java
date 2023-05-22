package dev.jaxydog.astral_additions.content.enchantment;

import dev.jaxydog.astral_additions.utility.Registerable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** An extension of a regular enchantment that provides additional functionality */
public class CustomEnchantment extends Enchantment implements Registerable.Main {

	/** The custom enchantment's inner raw identifier */
	private final String RAW_ID;
	/** The custom enchantment's configuration */
	private final CustomEnchantment.Config CONFIG;

	public CustomEnchantment(String rawId, CustomEnchantment.Config config) {
		super(config.getRarity(), config.getTarget(), config.getSlotTypes());
		this.RAW_ID = rawId;
		this.CONFIG = config;
	}

	@Override
	protected boolean canAccept(Enchantment other) {
		return this != other && !this.CONFIG.getEnchantmentBlacklist().contains(other);
	}

	@Override
	public int getMaxLevel() {
		return this.CONFIG.getMaxLevel();
	}

	@Override
	public int getMinLevel() {
		return this.CONFIG.getMinLevel();
	}

	@Override
	public int getMaxPower(int level) {
		return this.CONFIG.getMaxPower().apply(level);
	}

	@Override
	public int getMinPower(int level) {
		return this.CONFIG.getMinPower().apply(level);
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public boolean isCursed() {
		return this.CONFIG.isCurse();
	}

	@Override
	public boolean isTreasure() {
		return this.CONFIG.isTreasure();
	}

	@Override
	public void registerMain() {
		Main.super.registerMain();
		Registry.register(Registries.ENCHANTMENT, this.getId(), this);
	}

	/** Contains enchantment configuration options */
	public static class Config {

		private Rarity rarity = Rarity.COMMON;
		private EnchantmentTarget target = EnchantmentTarget.BREAKABLE;
		private EquipmentSlot[] slotTypes = new EquipmentSlot[] {};
		private int minLevel = 1;
		private int maxLevel = 5;
		private Function<Integer, Integer> minPower = level -> 1 + (level * 10);
		private Function<Integer, Integer> maxPower = level -> 1 + (level * 10) + 5;
		private List<Enchantment> blacklist = new ArrayList<>();
		private boolean isCurse = false;
		private boolean isTreasure = false;

		public Rarity getRarity() {
			return this.rarity;
		}

		public EnchantmentTarget getTarget() {
			return this.target;
		}

		public EquipmentSlot[] getSlotTypes() {
			return this.slotTypes;
		}

		public int getMaxLevel() {
			return this.maxLevel;
		}

		public int getMinLevel() {
			return this.minLevel;
		}

		public Function<Integer, Integer> getMaxPower() {
			return this.maxPower;
		}

		public Function<Integer, Integer> getMinPower() {
			return this.minPower;
		}

		public List<Enchantment> getEnchantmentBlacklist() {
			return this.blacklist;
		}

		public boolean isCurse() {
			return this.isCurse;
		}

		public boolean isTreasure() {
			return this.isTreasure;
		}

		public Config rarity(Rarity rarity) {
			this.rarity = rarity;
			return this;
		}

		public Config target(EnchantmentTarget target) {
			this.target = target;
			return this;
		}

		public Config slots(EquipmentSlot... slotTypes) {
			this.slotTypes = slotTypes;
			return this;
		}

		public Config levels(int minLevel, int maxLevel) {
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
			return this;
		}

		public Config power(Function<Integer, Integer> minPower, Function<Integer, Integer> maxPower) {
			this.minPower = minPower;
			this.maxPower = maxPower;
			return this;
		}

		public Config blacklists(Enchantment... enchantments) {
			this.blacklist = List.of(enchantments);
			return this;
		}

		public Config curse() {
			this.isCurse = true;
			return this;
		}

		public Config treasure() {
			this.isTreasure = true;
			return this;
		}
	}
}
