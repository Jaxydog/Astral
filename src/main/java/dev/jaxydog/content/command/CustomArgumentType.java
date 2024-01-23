package dev.jaxydog.content.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jaxydog.register.Registered;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

/** Allows the creation of command argument types using the auto-registration system */
public abstract class CustomArgumentType<T> implements ArgumentType<T>, Registered.Common {

	/** The custom argument type's inner raw identifier */
	private final String RAW_ID;

	public CustomArgumentType(String rawId) {
		this.RAW_ID = rawId;
	}

	@SuppressWarnings("RedundantThrows")
	@Override
	public abstract T parse(StringReader reader) throws CommandSyntaxException;

	@Override
	public String getRegistryIdPath() {
		return this.RAW_ID;
	}

	@Override
	public void register() {
		ArgumentTypeRegistry.registerArgumentType(this.getRegistryId(),
			this.getClass(),
			ConstantArgumentSerializer.of(() -> this)
		);
	}

}
