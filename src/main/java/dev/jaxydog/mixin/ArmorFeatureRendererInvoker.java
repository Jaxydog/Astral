package dev.jaxydog.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;

/** Provides hooks to invoke private methods within the armor feature renderer */
@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public interface ArmorFeatureRendererInvoker {

    /** Invokes the 'renderArmorParts' method */
    @Invoker("renderArmorParts")
    public void invokeRenderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, ArmorItem item, BipedEntityModel<?> model, boolean secondTextureLayer,
            float red, float green, float blue, @Nullable String overlay);

    /** Invokes the 'setVisible' method */
    @Invoker("setVisible")
    public void invokeSetVisible(BipedEntityModel<?> bipedModel, EquipmentSlot slot);

    /** Invokes the 'usesInnerModel' method */
    @Invoker("usesInnerModel")
    public boolean invokeUsesInnerModel(EquipmentSlot slot);

}
