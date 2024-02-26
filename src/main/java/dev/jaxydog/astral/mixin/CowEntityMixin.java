package dev.jaxydog.astral.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.utility.CowType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends PassiveEntityMixin {

    // spiders 🕷️ 🕸️
    protected CowEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private boolean isCowType(LivingEntity entity, CowType type) {
        return entity.getDataTracker().get(CowType.COW_TYPE).equals(type.asString());
    }

    @SuppressWarnings("SameParameterValue") // in case.
    @Unique
    private boolean isCowType(CowType type) {
        return this.isCowType(this, type);
    }

    @Unique
    private CowType getCowType(LivingEntity entity) {
        return CowType.fromName(entity.getDataTracker().get(CowType.COW_TYPE));
    }

    @Unique
    private CowType getCowType() {
        return this.getCowType(this);
    }

    @Unique
    private void setCowType(LivingEntity entity, CowType type) {
        entity.getDataTracker().set(CowType.COW_TYPE, type.asString());
    }

    @Unique
    private void setCowType(CowType type) {
        this.setCowType(this, type);
    }

    @Shadow
    public abstract @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity);

    @Override
    protected void pinkCowRng(CowEntity cow) {
        if (this.getRandom().nextInt(124) == 0) {
            this.dataTracker.set(CowType.COW_TYPE, CowType.PINK.asString());
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void pinkMilk(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfo) {
        final ItemStack stack = player.getStackInHand(hand);

        if (!stack.isOf(Items.GLASS_BOTTLE) || this.isBaby() || !this.isCowType(CowType.PINK)) return;

        final ItemStack result = AstralItems.STRAWBERRY_MILK.getDefaultStack();
        final ItemStack exchanged = ItemUsage.exchangeStack(stack, player, result, false);

        player.setStackInHand(hand, exchanged);
        player.playSound(SoundEvents.ENTITY_COW_MILK, 1F, 1F);

        callbackInfo.setReturnValue((ActionResult.success(player.getWorld().isClient())));
    }

    @ModifyReturnValue(
        method = "createChild(Lnet/minecraft/server/world/ServerWorld;"
            + "Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/CowEntity;",
        at = @At("RETURN")
    )
    private @Nullable CowEntity createVariedChild(
        CowEntity baby, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) PassiveEntity entity
    ) {
        if (baby == null) return null;

        final CowType typeA = this.getCowType();
        final CowType typeB = this.getCowType(entity);
        final boolean usePink;

        if (typeA.equals(typeB)) {
            usePink = typeA.equals(CowType.PINK) ? random.nextBoolean() : random.nextInt(124) == 0;
        } else {
            usePink = random.nextInt(2) == 0;
        }

        this.setCowType(baby, usePink ? CowType.PINK : CowType.BROWN);

        return baby;
    }

}
