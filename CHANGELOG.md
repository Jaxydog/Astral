# Release 1.4.0

### Internal changes

- The mod's license has been updated to AGPL-3.0 or higher.
- The formatting of all files has been updated and normalized.
- Simplified the logic of some code, such as removing an extra variable assignment within `RamImpactTaskMixin`'s
  injection method.
- Fixed multiple spelling errors, most notably correcting the spelling of "gibbous" in `astral:moon_phase`
- `RegisterableMap` has been updated.
    - Most constructor arguments have been extracted into abstract methods.
    - Values now have lazy initialization, only being constructed when they are registered or any value is fetched.
- Some static constants have been relocated.
    - `NbtUtil.CUSTOM_MODEL_DATA_KEY` has been moved to `Customized.CUSTOM_MODEL_DATA_KEY`.
    - `NbtUtil.SET_GLINT_KEY` has been moved to `ItemMixin.SET_GLINT_KEY`.
- Some static constants have been renamed.
    - `CustomBlocks.DYED_AMETHYST_BLOCK_MAP` has been renamed to `DYED_AMETHYST_BLOCKS`.
    - `CustomBlocks.DYED_AMETHYST_CLUSTER_BLOCK_MAP` has been renamed to `DYED_AMETHYST_CLUSTER_BLOCKS`.
    - `CustomItems.CLOUDY_ARMOR_MAP` has been renamed to `CLOUDY_ARMOR`.
    - `CustomItems.DYED_AMETHYST_BLOCK_MAP` has been renamed to `DYED_AMETHYST_BLOCKS`.
    - `CustomItems.DYED_AMETHYST_CLUSTER_MAP` has been renamed to `DYED_AMETHYST_CLUSTERS`.
- `ItemStack`s now have the following methods added via interface injection:
    - `astral$setItem` sets the `ItemStack`'s inner `Item` reference to a different item (e.g. a stack of 12 eggs -> a
      stack of 12 snowballs).
    - `astral$copyWithItem` makes a copy of the item stack with the inner `Item` reference changed into a different
      item.
- `Cloudy` has been reworked (mostly) from the ground up.
    - All methods now take an `ItemStack` argument.
    - `MAX_STORMINESS` and `MIN_STORMINESS` have been replaced by the `getMaxStorminess` and `getMinStorminess` methods
      respectively.
    - `getStorminessLabelKey` has been replaced by the static constant `STORMINESS_LABEL_KEY`.
    - `clampStorminess` now accounts for floating-point imprecision.
    - `updateStormines` now handles increasing and decreasing storminess values slightly differently.

### Content changes

- Created `astral:placeholder` (`PlaceholderItem`), which is a dummy item that dynamically changes its model and item
  name depending on NBT.
    - Added an "Enderman Plush" model.
    - Added a "Kiwi Plush" model.
    - Added a "Handmade Mirror" model.
    - Added a "Hatsune Miku Pop! Figurine" model.
    - Added a "Coil Head Plush" model.
    - Added a "Biblically-Accurate Spiral Plush" model.
- Added a "Sylvian Knife" model for Farmer's Delight's netherite knives.
- Created `PlaceholderMimicItem`, which nearly perfectly imitates another already-defined item, while *also*
  extending `PlaceholderItem`.
- Updated cloudy items
    - All items will automatically remove their Storminess NBT when the value stored is `0F`.
    - All items have had their increase / decrease rates modified slightly.
- Updated dyed amethyst blocks.
    - Must be harvested using a pickaxe.
    - Outputs chime sound effects when walked on.
    - Supports universal dyeing.
    - Grouped together within the recipe book.
- Updated dyed amethyst clusters.
    - Must be harvested using a pickaxe.
    - Supports universal dyeing.
    - Grouped together within the recipe book.
- Updated randomizer blocks.
    - Craftable using 8 iron blocks and 1 redstone torch.
    - Textures have been modified.
    - Turns red in "boolean mode" as a more apparent visual indicator.
    - Must be harvested using a stone pickaxe or higher.
- Lightning bolt entities now support the `{PreserveItems:1b}` NBT tag, which prevents hit items from being discarded.

## Patch 1.4.1

- Fixed the "Sylvian Knife", which was not properly overwritten in the previous version.

## Patch 1.4.2

- Removed the "Sylvian Knife" model, as it's still not displaying properly.
- Seeds used to feed damaged parrots are now specified within `#astral:parrot_feed`.
- Simplified parrot mixin sound playing.
- Removed excess mixin hooks from `PlayerInventoryMixin`.

## Patch 1.4.3

- Fixes a potential memory leak caused by the goat's ram impact task mixin.

# Release 1.3.0

### Internal Changes

- Created `RegisterableMap`, a generic abstract class that defines common functionality for classes that generate and
  contain `Registerable` values.
- `Registerable.Datagen` has been updated.
    - Arguments have been changed to correctly correlate to data-generator functionality.
    - Properly handle automatic registration within the `ContentContainer` class.

### Content Changes

- Updated dyed amethyst blocks.
    - Added loot tables allowing block harvesting.
- Updated dyed amethyst clusters.
    - Added loot tables allowing block harvesting.

## Patch 1.3.1

### Internal Changes

- `RegisterableMap` has been updated.
    - Implementations must specify an `Identifier` generation `BiFunction` within their super constructor.
    - Implementations must specify a `Comparator` function to determine registration ordering.

# Release 1.2.0

### Internal Changes

- Add support for a data-driven currency system, replacing the old currency system.

### Content Changes

- `astral:maws_hate_it` has been replaced by the `astral:volcanoverpowered` advancement.
- Updated cloudy armor.
    - The duration of the armor's provided effect has been reduced to 20 ticks instead of 60.

## Patch 1.2.1

### Content Changes

- Updated `astral:distance`
    - Fixed a crash caused by improper position parsing.

## Patch 1.2.2

### Internal Changes

- The formatting of all files has been updated and normalized.
- Most local variables that aren't modified are now `final`.
- Updated `ArmorFeatureRendererMixin`.
    - Rewrite to be less intrusive.
- Updated `LivingEntityMixin`.
    - Check all spawned mobs and only scale health if they *should* be scaled.

### Content Changes

- Removed unused assets.

## Patch 1.2.3

### Content Changes

- Updated cloudy items.
    - Added recipe advancements.
- Updated dyed amethyst blocks.
    - Added crafting recipes.
    - Added recipe advancements.
- Updated dyed amethyst clusters.
    - Added crafting recipes.
    - Added recipe advancements.

# Release 1.1.0

### Internal Changes

- Major rework and update to mob challenge scaling.
    - Implemented challenge scaling for all mobs that can damage the player.
    - Modified multiple mixins to be less intrusive.
    - The power of all scaled explosions has been capped.
    - Halved the rate of scaling in dimensions other than the overworld.
    - Fixed a bug that prevented mobs from spawning with the correct health values.
    - Fixed a bug that prevented a mob's health from updating when toggling whether scaling is enabled.
    - Ensured that health values are only modified by the server.
    - Changed the `challengeAttackAdditive` and `challengeHealthAdditive` gamerules into `double`s instead of `int`s.
    - Added the `challengeUseWorldspawn` gamerule, which, if false, centers scaling around 0, 0 instead of the world
      spawnpoint.
    - Added the entity type tag `astral:challenge`, which is used to determine whether an entity has scaling rules
      applied.
    - All `fireball`, `small_fireball`, and `arrow` entities are now scaled if their source entity should be scaled.

## Patch 1.1.1

#### Initial public release!

### Internal Changes

- Updated mob challenge scaling.
    - Only scale if the entity is non-null (fixes an edge-case that leads to a crash).
    - Skip scaling if the entity contains the NBT tag `{IgnoreChallengeScaling:1b}`

### Content Changes

- Updated cloudy items.
    - All items now dry faster if the entity holding them is on fire.
- Updated cloudy armor.
    - Add model textures.
    - Apply jump boost if the average storminess is <50%, otherwise apply slowness.

# Release 1.0.0

#### Initial private release!

> Welcome to pre-history...
>
> Version 1.0.0 was the subject of my rapid-development process, where I fixed bugs and recompiled constantly without
> effectively keeping records outside of conversations on Discord. Even the changelogs for 1.1.0 and 1.1.1 are difficult
> to put together, as they were also used exclusively for private testing and bug-hunting.
>
> Because of this ever-changing amorphous version, it's near impossible to accurately and effectively keep track of all
> changes, as they were often major, extensive updates.
>
> As such, I won't bother tracking it. At this point the mod was only being used within our private testing servers
> regardless. If you want to view the slew of changes and modifications to this version, feel free to look at the commit
> history.
