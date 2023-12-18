package dev.jaxydog.content.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jaxydog.utility.register.Registerable;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

/** Allows the creation of command argument types using the auto-registration system */
public abstract class CustomArgumentType<T> implements ArgumentType<T>, Registerable.Main {

	/** The custom argument type's inner raw identifier */
	private final String RAW_ID;

	public CustomArgumentType(String rawId) {
		this.RAW_ID = rawId;
	}

	@SuppressWarnings("RedundantThrows")
	@Override
	public abstract T parse(StringReader reader) throws CommandSyntaxException;

	@Override
	public String getRawId() {
		return this.RAW_ID;
	}

	@Override
	public void registerMain() {
		ArgumentTypeRegistry.registerArgumentType(this.getId(),
			this.getClass(),
			ConstantArgumentSerializer.of(() -> this)
		);
	}

}
