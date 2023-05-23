package dev.jaxydog.astral.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Provides hooks to invoke private methods within the armor feature renderer */
@Mixin(ArmorFeatureRenderer.class)
public interface ArmorFeatureRendererInvoker {
	@Invoker("renderArmorParts")
	public void invokeRenderArmorParts(
		MatrixStack stack,
		VertexConsumerProvider provider,
		int light,
		ArmorItem item,
		boolean glint,
		BipedEntityModel<?> model,
		boolean secondLayer,
		float r,
		float g,
		float b,
		@Nullable String overlay
	);

	@Invoker("setVisible")
	public void invokeSetVisible(BipedEntityModel<?> bipedModel, EquipmentSlot slot);

	@Invoker("usesInnerModel")
	public boolean invokeUsesInnerModel(EquipmentSlot slot);
}
