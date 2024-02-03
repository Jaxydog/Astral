package dev.jaxydog.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.jaxydog.content.power.custom.ConditionedCooldownPower;
import io.github.apace100.apoli.command.PowerOperation;
import io.github.apace100.apoli.power.Power;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PowerOperation.class)
public abstract class PowerOperationMixin implements ArgumentType<PowerOperation.Operation> {

    @Shadow
    @Final
    public static SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION;

    @Inject(method = "lambda$parse$0", at = @At("TAIL"))
    private static void assignConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.setCooldown(score.getScore());
        }
    }

    @Inject(method = "lambda$parse$1", at = @At("TAIL"))
    private static void addAssignConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.modify(score.getScore());
        }
    }

    @Inject(method = "lambda$parse$2", at = @At("TAIL"))
    private static void subAssignConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.modify(-score.getScore());
        }
    }

    @Inject(method = "lambda$parse$3", at = @At("TAIL"))
    private static void mulAssignConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.modify(cooldownPower.getRemainingTicks() * score.getScore());
        }
    }

    @Inject(method = "lambda$parse$4", at = @At("TAIL"))
    private static void divAssignConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) throws CommandSyntaxException {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            final int remaining = cooldownPower.getRemainingTicks();
            final int divisor = score.getScore();

            if (divisor == 0) throw DIVISION_ZERO_EXCEPTION.create();

            cooldownPower.setCooldown(Math.floorDiv(remaining, divisor));
        }
    }

    @Inject(method = "lambda$parse$5", at = @At("TAIL"))
    private static void remAssignConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) throws CommandSyntaxException {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            final int remaining = cooldownPower.getRemainingTicks();
            final int divisor = score.getScore();

            if (divisor == 0) throw DIVISION_ZERO_EXCEPTION.create();

            cooldownPower.setCooldown(Math.floorMod(remaining, divisor));
        }
    }

    @Inject(method = "lambda$parse$6", at = @At("TAIL"))
    private static void minConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.setCooldown(Math.min(cooldownPower.getRemainingTicks(), score.getScore()));
        }
    }

    @Inject(method = "lambda$parse$7", at = @At("TAIL"))
    private static void maxConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            cooldownPower.setCooldown(Math.max(cooldownPower.getRemainingTicks(), score.getScore()));
        }
    }

    @Inject(method = "lambda$parse$8", at = @At("TAIL"))
    private static void swapConditionedCooldown(
        Power power, ScoreboardPlayerScore score, CallbackInfo callbackInfo
    ) {
        if (power instanceof final ConditionedCooldownPower cooldownPower) {
            final int value = score.getScore();

            score.setScore(cooldownPower.getRemainingTicks());
            cooldownPower.setCooldown(value);
        }
    }

}
