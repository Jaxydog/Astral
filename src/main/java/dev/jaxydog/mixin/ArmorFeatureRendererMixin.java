package dev.jaxydog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import blue.endless.jankson.annotation.Nullable;
import dev.jaxydog.content.item.CustomArmorItem;
import dev.jaxydog.content.item.color.ColoredArmorItem;
import dev.jaxydog.utility.ColorUtil.RGB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;

/** Implements multiple texture layers and coloring for custom armor items */
@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

	@Invoker("renderArmorParts")
	public abstract void renderArmorPartsInvoker(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
		int light, ArmorItem item, A model, boolean inner, float red, float green, float blue,
		@Nullable String overlay);

	@Invoker("renderGlint")
	public abstract void renderGlintInvoker(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
		A model);

	@Invoker("renderTrim")
	public abstract void renderTrimInvoker(ArmorMaterial material, MatrixStack matrices,
		VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings);

	@Invoker("usesInnerModel")
	public abstract boolean usesInnerModelInvoker(EquipmentSlot slot);

	@Inject(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;usesInnerModel(Lnet/minecraft/entity/EquipmentSlot;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void newRenderArmorInject(MatrixStack matrix, VertexConsumerProvider vertex, T entity,
		EquipmentSlot slot, int light, A model, CallbackInfo callbackInfo, ItemStack stack,
		ArmorItem item) {
		if (!(item instanceof final CustomArmorItem custom)) {
			return;
		}

		final boolean inner = this.usesInnerModelInvoker(slot);
		final int layers = custom.getTextureLayers();

		for (int index = 0; index < layers; index += 1) {
			final String id = String.valueOf(index);
			final float r, g, b;

			if (custom instanceof final ColoredArmorItem colored) {
				final RGB color = RGB.from(colored.getColor(stack, index));

				r = ((float) color.getR()) / 255F;
				g = ((float) color.getG()) / 255F;
				b = ((float) color.getB()) / 255F;
			} else {
				r = 1F;
				g = 1F;
				b = 1F;
			}

			this.renderArmorPartsInvoker(matrix, vertex, light, custom, model, inner, r, g, b, id);
		}

		ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), stack).ifPresent(trim -> {
			final ArmorMaterial material = custom.getMaterial();

			this.renderTrimInvoker(material, matrix, vertex, light, trim, model, inner);
		});

		if (stack.hasGlint()) {
			this.renderGlintInvoker(matrix, vertex, light, model);
		}

		callbackInfo.cancel();
	}

}
