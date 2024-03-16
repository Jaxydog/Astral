package dev.jaxydog.astral.content;

import dev.jaxydog.astral.content.block.AstralBlocks;
import dev.jaxydog.astral.content.data.CustomData;
import dev.jaxydog.astral.content.effect.CustomPotions;
import dev.jaxydog.astral.content.effect.CustomStatusEffects;
import dev.jaxydog.astral.content.item.AstralItems;
import dev.jaxydog.astral.content.item.group.AstralItemGroups;
import dev.jaxydog.astral.content.power.CustomActions;
import dev.jaxydog.astral.content.power.CustomConditions;
import dev.jaxydog.astral.content.power.CustomPowers;
import dev.jaxydog.astral.content.sound.CustomSoundEvents;
import dev.jaxydog.astral.content.trinket.CustomTrinketPredicates;
import dev.jaxydog.astral.register.ContentRegistrar;
import dev.jaxydog.astral.register.IgnoreRegistration;
import dev.jaxydog.astral.register.RegistrationPriority;

/** Contains all instances of defined content container classes */
@SuppressWarnings("unused")
public final class CustomContent extends ContentRegistrar {

    /** Stores the custom content container instance */
    @IgnoreRegistration
    public static final CustomContent INSTANCE = new CustomContent();

    // Origins extensions
    public static final CustomActions ACTIONS = new CustomActions();
    public static final CustomConditions CONDITIONS = new CustomConditions();
    @RegistrationPriority(1)
    public static final CustomData DATA = new CustomData();
    public static final CustomPowers POWERS = new CustomPowers();

    // Vanilla extensions
    @RegistrationPriority(2)
    public static final AstralBlocks BLOCKS = new AstralBlocks();
    public static final CustomGamerules GAMERULES = new CustomGamerules();
    @RegistrationPriority(1)
    public static final AstralItemGroups ITEM_GROUPS = new AstralItemGroups();
    @RegistrationPriority(1)
    public static final AstralItems ITEMS = new AstralItems();
    public static final CustomPotions POTIONS = new CustomPotions();
    public static final CustomSoundEvents SOUND_EVENTS = new CustomSoundEvents();
    @RegistrationPriority(1)
    public static final CustomStatusEffects STATUS_EFFECTS = new CustomStatusEffects();

    // Miscellaneous
    public static final CustomTrinketPredicates TRINKET_SLOTS = new CustomTrinketPredicates();

}
