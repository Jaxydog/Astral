package dev.jaxydog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
import net.minecraft.item.ItemStack;

/** Implements multiple texture layers and coloring for custom armor items */
@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

	/** Returns the mixin's 'this' instance */
	@SuppressWarnings("unchecked")
	private final ArmorFeatureRenderer<T, M, A> self() {
		return (ArmorFeatureRenderer<T, M, A>) (Object) this;
	}

	/** Returns the mixin's invoker */
	private final ArmorFeatureRendererInvoker invoker() {
		return (ArmorFeatureRendererInvoker) this.self();
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private void renderArmorInject(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo callbackInfo) {
		final ItemStack equipped = entity.getEquippedStack(armorSlot);

		if (!(equipped.getItem() instanceof CustomArmorItem armorItem)) {
			return;
		}
		if (armorItem.getSlotType() != armorSlot) {
			return;
		}

		final ArmorFeatureRenderer<T, M, A> self = this.self();
		final ArmorFeatureRendererInvoker invoker = this.invoker();

		self.getContextModel().copyBipedStateTo(model);
		invoker.invokeSetVisible(model, armorSlot);

		final int layers = armorItem.getTextureLayers();
		final boolean inner = invoker.invokeUsesInnerModel(armorSlot);

		for (int index = 0; index < layers; index += 1) {
			final String label = String.valueOf(index);
			final float r, g, b;

			if (armorItem instanceof ColoredArmorItem colored) {
				RGB color = RGB.from(colored.getColor(equipped, index));

				r = ((float) color.getR()) / 255.0F;
				g = ((float) color.getG()) / 255.0F;
				b = ((float) color.getB()) / 255.0F;
			} else {
				r = 1.0F;
				g = 1.0F;
				b = 1.0F;
			}

			invoker.invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, model,
					inner, r, g, b, label);
		}

		callbackInfo.cancel();
	}

}
