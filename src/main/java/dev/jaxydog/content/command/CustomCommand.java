package dev.jaxydog.content.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/** Allows the creation of commands using the auto-registration system */
public abstract class CustomCommand implements Registerable.Main {

	/** The custom command's inner raw identifier */
	private final String RAW_ID;

	public CustomCommand(String rawId) {
		this.RAW_ID = rawId;
	}

	/** Executes the command */
	public abstract int execute(CommandContext<ServerCommandSource> context)
			throws CommandSyntaxException;

	/** A predicate that determines if a source can use the command */
	public abstract boolean requires(ServerCommandSource source);

	/** Returns the command's builder */
	public LiteralArgumentBuilder<ServerCommandSource> getCommand() {
		return CommandManager.literal(this.getRawId()).requires(this::requires);
	}

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> dispatcher
				.register(this.getCommand().executes(this::execute)));
	}

}
