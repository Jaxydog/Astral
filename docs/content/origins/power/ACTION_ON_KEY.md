# Action on Key

An extension of `origins:active_self` that has a variety of extra fields added onto it.

See [Ticking Cooldown](TICKING_COOLDOWN.md) for examples related to the cooldown component of this power.

Type ID: `astral:action_on_key`

### Fields

| Field            | Type             | Default                                   | Description                                                                                                                                          |
|------------------|------------------|-------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cooldown`       | Integer          |                                           | The total duration in ticks that this power takes to recharge.                                                                                       |
| `entity_action`  | Entity Action    |                                           | The action to execute on the player.                                                                                                                 |
| `hud_render`     | Hud Render       | `{ "should_render": false }`              | Determines how the cooldown of this power is visualized on the HUD.                                                                                  |
| `key`            | Key              | `{ "key": "key.origins.primary_active" }` | Which active key this power should respond to.                                                                                                       |
| `tick_condition` | Entity Condition | *optional*                                | If specified, the cooldown will only progress while the condition holds true.                                                                        |
| `min_action`     | Entity Action    | *optional*                                | Executes an action on the entity that holds the power when the cooldown is fully triggered.                                                          |
| `set_action`     | Entity Action    | *optional*                                | Executes an action on the entity that holds the power whenever the cooldown is set to any duration, **even if it has not yet recharged beforehand**. |
| `max_action`     | Entity Action    | *optional*                                | Executes an action on the entity that holds the power when the cooldown has recharged.                                                               |

### Examples

```json
{
    "type": "astral:action_on_key",
    "cooldown": 300,
    "entity_action": {
        "type": "origins:raycast",
        "distance": 16.0,
        "entity": false,
        "command_at_hit": "tp @s ~ ~ ~",
        "hit_action": {
            "type": "origins:execute_command",
            "command": "playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1 1.5 1"
        }
    },
    "max_action": {
        "type": "origins:execute_command",
        "command": "playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1 2 1"
    }
}
```

Teleports the player up to 16 blocks away when the primary key is pressed, playing a chime sound when the cooldown has
recharged.

```json
{
    "type": "astral:action_on_key",
    "cooldown": 300,
    "entity_action": {
        "type": "origins:add_velocity",
        "y": 10,
        "set": true
    },
    "hud_render": {
        "bar_index": 5
    },
    "tick_condition": {
        "type": "origins:on_block",
        "block_condition": {
            "type": "origins:movement_blocking"
        }
    },
    "condition": {
        "type": "origins:on_block",
        "block_condition": {
            "type": "origins:movement_blocking"
        }
    }
}
```

Shoots the player into the air when the primary key is pressed, only recharging and / or activating whilst standing on a
movement-blocking (solid) block.
