package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.content.item.CustomArmorItem;
import dev.jaxydog.content.item.color.ColoredArmorItem;
import dev.jaxydog.content.trinket.CustomTrinketPredicates;
import dev.jaxydog.utility.ColorUtil.Rgb;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** Implements multiple texture layers and coloring for custom armor items */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
	extends FeatureRenderer<T, M> {

	public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Shadow
	protected abstract boolean usesInnerModel(EquipmentSlot slot);

	@Shadow
	protected abstract void renderArmorParts(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ArmorItem item,
		A model,
		boolean secondTextureLayer,
		float red,
		float green,
		float blue,
		@org.jetbrains.annotations.Nullable String overlay
	);

	@Shadow
	protected abstract void renderTrim(
		ArmorMaterial material,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ArmorTrim trim,
		A model,
		boolean leggings
	);

	@Shadow
	protected abstract void renderGlint(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model
	);

	@Shadow
	protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

	@ModifyVariable(method = "renderArmor", at = @At("STORE"))
	private ItemStack renderArmorInject(ItemStack equippedStack, @Local T entity) {
		final ItemStack cosmeticStack = CustomTrinketPredicates.getCosmeticHelmet(entity);

		if (cosmeticStack.isEmpty() || equippedStack.isIn(CustomTrinketPredicates.COSMETIC_HELMET_UNHIDEABLE)) {
			return equippedStack;
		} else {
			return cosmeticStack;
		}
	}

	@SuppressWarnings({ "unchecked", "UnstableApiUsage" })
	@Inject(
		method = "renderArmor", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;usesInnerModel(Lnet/minecraft/entity/EquipmentSlot;)Z"
	), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true
	)
	private void newRenderArmorInject(
		MatrixStack matrix,
		VertexConsumerProvider vertex,
		T entity,
		EquipmentSlot slot,
		int light,
		A model,
		CallbackInfo callbackInfo,
		ItemStack stack,
		ArmorItem item
	) {
		if (!(item instanceof final CustomArmorItem custom)) return;

		final ArmorRenderer renderer = ArmorRendererRegistryImpl.get(stack.getItem());

		if (renderer != null) {
			final BipedEntityModel<LivingEntity> rendererModel = (BipedEntityModel<LivingEntity>) this.getContextModel();

			renderer.render(matrix, vertex, stack, entity, slot, light, rendererModel);
			callbackInfo.cancel();

			return;
		}

		final boolean inner = this.usesInnerModel(slot);
		final int layers = custom.getTextureLayers();

		for (int index = 0; index < layers; index += 1) {
			final String id = String.valueOf(index);
			final float r, g, b;

			if (custom instanceof final ColoredArmorItem colored) {
				final Rgb color = new Rgb(colored.getColor(stack, index));

				r = ((float) color.r()) / 255F;
				g = ((float) color.g()) / 255F;
				b = ((float) color.b()) / 255F;
			} else {
				r = 1F;
				g = 1F;
				b = 1F;
			}

			this.renderArmorParts(matrix, vertex, light, custom, model, inner, r, g, b, id);
		}

		ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), stack).ifPresent(trim -> {
			final ArmorMaterial material = custom.getMaterial();

			this.renderTrim(material, matrix, vertex, light, trim, model, inner);
		});

		if (stack.hasGlint()) this.renderGlint(matrix, vertex, light, model);

		callbackInfo.cancel();
	}

}
