package dev.jaxydog.astral.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.astral.content.trinket.CustomTrinketPredicates;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeadFeatureRenderer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead>
    extends FeatureRenderer<T, M> {

    public HeadFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyVariable(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
        at = @At("STORE")
    )
    private ItemStack cosmeticReplacer(ItemStack equippedStack, @Local(argsOnly = true) T entity) {
        final ItemStack cosmeticStack = CustomTrinketPredicates.getCosmeticHelmet(entity);

        if (cosmeticStack.isEmpty() || equippedStack.isIn(CustomTrinketPredicates.COSMETIC_HELMET_UNHIDEABLE)) {
            return equippedStack;
        } else {
            return cosmeticStack;
        }
    }

}
