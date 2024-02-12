package dev.jaxydog.content;

import dev.jaxydog.content.block.CustomBlocks;
import dev.jaxydog.content.command.CustomArgumentTypes;
import dev.jaxydog.content.command.CustomCommands;
import dev.jaxydog.content.data.CustomData;
import dev.jaxydog.content.effect.CustomPotions;
import dev.jaxydog.content.effect.CustomStatusEffects;
import dev.jaxydog.content.entity.CustomEntityTypes;
import dev.jaxydog.content.item.CustomItemGroups;
import dev.jaxydog.content.item.CustomItems;
import dev.jaxydog.content.power.CustomActions;
import dev.jaxydog.content.power.CustomConditions;
import dev.jaxydog.content.power.CustomPowers;
import dev.jaxydog.content.sound.CustomSoundEvents;
import dev.jaxydog.content.trinket.CustomTrinketPredicates;
import dev.jaxydog.register.ContentRegistrar;
import dev.jaxydog.register.IgnoreRegistration;

/** Contains all instances of defined content container classes */
public final class CustomContent extends ContentRegistrar {

    /** Stores the custom content container instance */
    @IgnoreRegistration
    public static final CustomContent INSTANCE = new CustomContent();

    // Defined custom content classes
    public static final CustomActions ACTIONS = new CustomActions();
    public static final CustomArgumentTypes ARGUMENT_TYPES = new CustomArgumentTypes();
    public static final CustomBlocks BLOCKS = new CustomBlocks();
    public static final CustomCommands COMMANDS = new CustomCommands();
    public static final CustomConditions CONDITIONS = new CustomConditions();
    public static final CustomData DATA = new CustomData();
    public static final CustomEntityTypes ENTITY_TYPES = new CustomEntityTypes();
    public static final CustomGamerules GAMERULES = new CustomGamerules();
    public static final CustomItemGroups ITEM_GROUPS = new CustomItemGroups();
    public static final CustomItems ITEMS = new CustomItems();
    public static final CustomPotions POTIONS = new CustomPotions();
    public static final CustomPowers POWERS = new CustomPowers();
    public static final CustomSoundEvents SOUND_EVENTS = new CustomSoundEvents();
    public static final CustomStatusEffects STATUS_EFFECTS = new CustomStatusEffects();
    public static final CustomTrinketPredicates TRINKET_SLOTS = new CustomTrinketPredicates();

}
