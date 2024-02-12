package dev.jaxydog.mixin;

import dev.jaxydog.utility.injected.AstralLightningEntity;
import dev.jaxydog.utility.injected.SprayableEntity;
import dev.onyxstudios.cca.api.v3.component.ComponentAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, ComponentAccess {

    @Shadow
    public abstract World getWorld();

    @SuppressWarnings("RedundantCast")
    @Inject(method = "onStruckByLightning", at = @At("HEAD"), cancellable = true)
    private void onStruckByLightningInject(ServerWorld world, LightningEntity bolt, CallbackInfo callbackInfo) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            if (((AstralLightningEntity) bolt).astral$preservesItems()) callbackInfo.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo callbackInfo) {
        if (this.getWorld().isClient()) return;
        if (!(this instanceof final SprayableEntity sprayable)) return;

        sprayable.astral$sprayTick();
    }

}
