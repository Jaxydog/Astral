package dev.jaxydog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jaxydog.content.power.custom.ConditionedCooldownPower;
import io.github.apace100.apoli.command.PowerOperation.Operation;
import io.github.apace100.apoli.command.ResourceCommand;
import io.github.apace100.apoli.command.ResourceCommand.SubCommand;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceCommand.class)
public class ResourceCommandMixin {

    @Inject(
        method = "resource", at = @At(
        value = "INVOKE_ASSIGN",
        target = "Lio/github/apace100/apoli/component/PowerHolderComponent;getPower(Lio/github/apace100/apoli/power/PowerType;)Lio/github/apace100/apoli/power/Power;",
        shift = Shift.AFTER
    ), cancellable = true
    )
    private static void resourceConditionedCooldown(
        CommandContext<ServerCommandSource> command,
        SubCommand sub,
        CallbackInfoReturnable<Integer> callbackInfo,
        @Local Entity player,
        @Local PowerType<?> powerType,
        @Local Power power
    ) throws CommandSyntaxException {
        if (!(power instanceof final ConditionedCooldownPower cooldownPower)) return;

        final int returnValue = switch (sub) {
            case HAS -> {
                command.getSource().sendFeedback(() -> Text.translatable("commands.execute.conditional.pass"), true);

                yield 1;
            }
            case GET -> {
                int ticks = cooldownPower.getRemainingTicks();

                command.getSource().sendFeedback(() -> Text.translatable(
                    "commands.scoreboard.players.get.success",
                    player.getEntityName(),
                    ticks,
                    powerType.getIdentifier().toString()
                ), true);

                yield ticks;
            }
            case SET -> {
                int ticks = IntegerArgumentType.getInteger(command, "value");

                cooldownPower.setCooldown(ticks);

                PowerHolderComponent.syncPower(player, powerType);

                command.getSource().sendFeedback(() -> Text.translatable(
                    "commands.scoreboard.players.set.success.single",
                    powerType.getIdentifier().toString(),
                    player.getEntityName(),
                    ticks
                ), true);

                yield 1;
            }
            case CHANGE -> {
                int ticks = IntegerArgumentType.getInteger(command, "value");

                cooldownPower.modify(ticks);

                PowerHolderComponent.syncPower(player, powerType);

                command.getSource().sendFeedback(() -> Text.translatable(
                    "commands.scoreboard.players.add.success.single",
                    ticks,
                    powerType.getIdentifier().toString(),
                    player.getEntityName(),
                    cooldownPower.getRemainingTicks()
                ), true);

                yield 1;
            }
            case OPERATION -> {
                final ScoreboardPlayerScore score = command.getSource().getServer().getScoreboard().getPlayerScore(
                    ScoreHolderArgumentType.getScoreHolder(command, "entity"),
                    ScoreboardObjectiveArgumentType.getObjective(command, "objective")
                );

                command.getArgument("operation", Operation.class).apply(cooldownPower, score);

                PowerHolderComponent.syncPower(player, powerType);

                command.getSource().sendFeedback(() -> Text.translatable(
                    "commands.scoreboard.players.operation.success.single",
                    powerType.getIdentifier().toString(),
                    player.getEntityName(),
                    cooldownPower.getRemainingTicks()
                ), true);

                yield 1;
            }
        };

        callbackInfo.setReturnValue(returnValue);
    }

}
