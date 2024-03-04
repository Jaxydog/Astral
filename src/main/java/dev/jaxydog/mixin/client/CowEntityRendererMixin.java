package dev.jaxydog.mixin.client;

import com.google.common.base.Suppliers;
import dev.jaxydog.Astral;
import dev.jaxydog.utility.CowType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(CowEntityRenderer.class)
public class CowEntityRendererMixin {

    @Unique
    private static final Identifier PINK_TEXTURE_DEFAULT = Astral.getId("textures/entity/cow/pink_cow.png");
    @Unique
    private static final Identifier PINK_TEXTURE_ETF = Astral.getId("textures/entity/cow/pink_cow_etf.png");
    @Unique
    private static final Supplier<Identifier> PINK_TEXTURE = Suppliers.memoize(() -> {
        if (FabricLoader.getInstance().getModContainer("entity_texture_features").isPresent()) {
            return PINK_TEXTURE_ETF;
        } else {
            return PINK_TEXTURE_DEFAULT;
        }
    });

    @Inject(
        method = "getTexture(Lnet/minecraft/entity/passive/CowEntity;)Lnet/minecraft/util/Identifier;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void maybePinkTexture(CowEntity cowEntity, CallbackInfoReturnable<Identifier> callbackInfo) {
        if (cowEntity.getDataTracker().get(CowType.COW_TYPE).equals(CowType.PINK.asString())) {
            callbackInfo.setReturnValue(PINK_TEXTURE.get());
        }
    }

}
