package dev.jaxydog.astral.content.trinket;

import com.mojang.datafixers.util.Function3;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import dev.jaxydog.astral.register.Registered;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class CustomTrinketPredicate implements Registered.Common {

	private final String RAW_ID;
	private final Function3<ItemStack, SlotReference, LivingEntity, TriState> SUPPLIER;

	public CustomTrinketPredicate(String rawId, Function3<ItemStack, SlotReference, LivingEntity, TriState> predicate) {
		this.RAW_ID = rawId;
		this.SUPPLIER = predicate;
	}

	@Override
	public String getRegistryIdPath() {
		return this.RAW_ID;
	}

	@Override
	public void register() {
		TrinketsApi.registerTrinketPredicate(this.getRegistryId(), this.SUPPLIER);
	}

}
