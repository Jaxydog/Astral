package dev.jaxydog.mixin;

import java.util.EnumMap;
import java.util.UUID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.jaxydog.content.item.CustomArmorMaterial;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorItem.Type;
import net.minecraft.item.Item.Settings;

/** Implements knockback resistance for custom armor items */
@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

	@Shadow
	@Final
	private static EnumMap<Type, UUID> MODIFIERS;

	@Shadow
	@Final
	@Mutable
	private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	@Shadow
	@Final
	private float knockbackResistance;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void constructor(ArmorMaterial material, Type type, Settings settings,
			CallbackInfo info) {
		if (!(material instanceof CustomArmorMaterial)) {
			return;
		}

		UUID uuid = MODIFIERS.get(type);
		var builder = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder();
		EntityAttributeModifier modifier = new EntityAttributeModifier(uuid,
				"Armor knockback resistance", this.knockbackResistance, Operation.ADDITION);

		this.attributeModifiers.forEach(builder::put);
		builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, modifier);
		this.attributeModifiers = builder.build();
	}

}
