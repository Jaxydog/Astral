package dev.jaxydog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/** Allows you to heal parrots with items */
@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin {

    /** Returns the mixin's 'this' instance */
    private ParrotEntity self() {
        return (ParrotEntity) (Object) this;
    }

    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void isBreedingItemInject(ItemStack stack,
            CallbackInfoReturnable<Boolean> callbackInfo) {
        callbackInfo.setReturnValue(stack.isOf(Items.WHEAT_SEEDS));
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void interactMobInject(PlayerEntity player, Hand hand,
            CallbackInfoReturnable<ActionResult> callbackInfo) {
        final ItemStack stack = player.getStackInHand(hand);
        final ParrotEntity self = this.self();
        final boolean missingHealth = self.getHealth() < self.getMaxHealth();
        final World world = player.getWorld();

        if (world.isClient || !self.isTamed() || !self.isBreedingItem(stack) || !missingHealth) {
            return;
        }

        if (!self.isSilent()) {
            self.getWorld().playSound(null, self.getX(), self.getY(), self.getZ(),
                    SoundEvents.ENTITY_PARROT_EAT, self.getSoundCategory(), 1.0f,
                    1.0f + (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2f);
        }
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        self.getWorld().sendEntityStatus(self,
                EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
        self.heal(1);
        callbackInfo.setReturnValue(ActionResult.SUCCESS);
    }

}
