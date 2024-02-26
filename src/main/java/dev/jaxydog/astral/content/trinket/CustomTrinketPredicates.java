package dev.jaxydog.astral.content.trinket;

import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.IgnoreRegistration;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Pair;

import java.util.Optional;

public final class CustomTrinketPredicates extends ContentRegistrar {

	@IgnoreRegistration
	public static final TagKey<Item> COSMETIC_HELMET_BLACKLIST = TagKey.of(Registries.ITEM.getKey(),
		Astral.getId("cosmetic_helmet_blacklist")
	);
	@IgnoreRegistration
	public static final TagKey<Item> COSMETIC_HELMET_UNHIDEABLE = TagKey.of(Registries.ITEM.getKey(),
		Astral.getId("cosmetic_helmet_unhideable")
	);

	public static final CustomTrinketPredicate COSMETIC_HELMET = new CustomTrinketPredicate("cosmetic_helmet",
		(stack, slot, entity) -> {
			if (stack.isIn(COSMETIC_HELMET_BLACKLIST)) return TriState.FALSE;
			if (MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.HEAD) return TriState.TRUE;

			return TriState.DEFAULT;
		}
	);

	public static ItemStack getCosmeticHelmet(LivingEntity entity) {
		final Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);

		return component.flatMap(c -> c.getEquipped(stack -> MobEntity.getPreferredEquipmentSlot(stack)
			== EquipmentSlot.HEAD).stream().filter(pair -> {
			final SlotType slot = pair.getLeft().inventory().getSlotType();

			return slot.getName().equals("cosmetic_helmet") && slot.getGroup().equals("head");
		}).map(Pair::getRight).findAny()).orElse(ItemStack.EMPTY);
	}

}
