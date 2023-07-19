package dev.jaxydog.content;

import dev.jaxydog.content.block.CustomBlocks;
import dev.jaxydog.content.command.CustomArgumentTypes;
import dev.jaxydog.content.command.CustomCommands;
import dev.jaxydog.content.data.CustomData;
import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.utility.register.ContentContainer;
import dev.jaxydog.utility.register.Skip;

/** Contains all instances of defined content container classes */
public final class CustomContent extends ContentContainer {

	/** Stores the custom content container instance */
	@Skip
	public static final CustomContent INSTANCE = new CustomContent();

	// Defined custom content classes
	public static final CustomGamerules GAMERULES = new CustomGamerules();
	public static final CustomData DATA = new CustomData();
	public static final CustomBlocks BLOCKS = new CustomBlocks();
	public static final CustomItems ITEMS = new CustomItems();
	public static final CustomCommands COMMANDS = new CustomCommands();
	public static final CustomArgumentTypes ARGUMENT_TYPES = new CustomArgumentTypes();

}
