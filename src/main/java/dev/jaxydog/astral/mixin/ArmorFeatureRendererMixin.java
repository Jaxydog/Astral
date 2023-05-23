package dev.jaxydog.astral.mixin;

import dev.jaxydog.astral.content.item.ColoredArmorItem;
import dev.jaxydog.astral.content.item.CustomArmorItem;
import dev.jaxydog.astral.utility.ColorHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Implements multiple texture layers and coloring for custom armor items */
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin {

	@SuppressWarnings("unchecked")
	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private <T extends LivingEntity, A extends BipedEntityModel<T>> void renderArmorInject(
		MatrixStack matrices,
		VertexConsumerProvider vertex,
		T entity,
		EquipmentSlot slot,
		int light,
		A model,
		CallbackInfo callbackInfo
	) {
		var self = (ArmorFeatureRenderer<T, ?, A>) (Object) this;
		var invoker = (ArmorFeatureRendererInvoker) self;
		var stack = ((LivingEntity) entity).getEquippedStack(slot);

		if (!(stack.getItem() instanceof CustomArmorItem)) return;

		var item = (CustomArmorItem) stack.getItem();

		if (item.getSlotType() != slot) return;

		self.getContextModel().copyBipedStateTo(model);
		invoker.invokeSetVisible(model, slot);

		var layers = item.getTextureLayers();
		var glint = stack.hasGlint();
		var inner = invoker.invokeUsesInnerModel(slot);

		if (item instanceof ColoredArmorItem) {
			for (var index = 0; index < layers; index += 1) {
				var color = ColorHelper.RGB.fromInt(((ColoredArmorItem) item).getColor(stack, index));

				var r = color.r();
				var g = color.g();
				var b = color.b();
				var l = "" + index;

				invoker.invokeRenderArmorParts(matrices, vertex, light, item, glint, model, inner, r, g, b, l);
			}
		} else {
			for (var index = 0; index < layers; index += 1) {
				var l = "" + index;

				invoker.invokeRenderArmorParts(matrices, vertex, light, item, glint, model, inner, 1, 1, 1, l);
			}
		}

		callbackInfo.cancel();
	}
}
