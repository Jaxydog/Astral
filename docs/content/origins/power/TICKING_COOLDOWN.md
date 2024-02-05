# Ticking Cooldown

An extension of `origins:cooldown` that has a variety of extra fields added onto it.

Type ID: `astral:ticking_cooldown`

### Fields

| Field            | Type             | Default                      | Description                                                                                                                                          |
|------------------|------------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cooldown`       | Integer          |                              | The total duration in ticks that this power takes to recharge.                                                                                       |
| `hud_render`     | Hud Render       | `{ "should_render": false }` | Determines how the cooldown of this power is visualized on the HUD.                                                                                  |
| `tick_condition` | Entity Condition | *optional*                   | If specified, the cooldown will only progress while the condition holds true.                                                                        |
| `min_action`     | Entity Action    | *optional*                   | Executes an action on the entity that holds the power when the cooldown is fully triggered.                                                          |
| `set_action`     | Entity Action    | *optional*                   | Executes an action on the entity that holds the power whenever the cooldown is set to any duration, **even if it has not yet recharged beforehand**. |
| `max_action`     | Entity Action    | *optional*                   | Executes an action on the entity that holds the power when the cooldown has recharged.                                                               |

### Examples

```json
{
    "type": "astral:ticking_cooldown",
    "cooldown": 200,
    "hud_render": {
        "bar_index": 5
    },
    "tick_condition": {
        "type": "origins:equipped_item",
        "equipment_slot": "chest",
        "item_condition": {
            "type": "origins:enchantment",
            "enchantment": "minecraft:protection",
            "comparison": ">",
            "compare_to": 0,
            "inverted": true
        }
    }
}
```

A cooldown of 200 ticks (10 seconds) that only recharges whilst the entity does not have a chestplate with any level
of protection equipped.

```json
{
    "type": "astral:ticking_cooldown",
    "cooldown": 60,
    "max_action": {
        "type": "origins:trigger_cooldown",
        "power": "astral:ticking_cooldown_example"
    },
    "set_action": {
        "type": "origins:execute_command",
        "command": "tellraw @s \"This only triggers when being set directly, not whilst ticking!\""
    }
}
```

A cooldown of 60 ticks (3 seconds) that repeatedly triggers itself and outputs "This only triggers when being set
directly, not whilst ticking!" to the entity's chat when reset.
