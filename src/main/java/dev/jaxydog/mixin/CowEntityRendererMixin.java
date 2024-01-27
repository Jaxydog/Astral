package dev.jaxydog.mixin;

import dev.jaxydog.Astral;
import dev.jaxydog.utility.CowType;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntityRenderer.class)
public class CowEntityRendererMixin {

	@Unique
	private static final Identifier PINK_TEXTURE = Astral.getId("textures/entity/cow/pink_cow.png");

	@Inject(
		method = "getTexture(Lnet/minecraft/entity/passive/CowEntity;)Lnet/minecraft/util/Identifier;",
		at = @At("HEAD"),
		cancellable = true
	)
	private void getTextures(CowEntity cowEntity, CallbackInfoReturnable<Identifier> callbackInfo) {
		if (cowEntity.getDataTracker().get(CowType.COW_TYPE).equals(CowType.PINK.asString())) {
			callbackInfo.setReturnValue(PINK_TEXTURE);
		}
	}

}
