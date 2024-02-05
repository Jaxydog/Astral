# Repeat

Repeats the provided action the specified number of times.

Type ID: `astral:repeat`

### Fields

| Field    | Type    | Default | Description                               |
|----------|---------|---------|-------------------------------------------|
| `repeat` | Integer |         | The number of times to repeat the action. |
| `action` | Action  |         | The action to repeat.                     |

### Examples

```json
{
    "entity_action": {
        "type": "astral:repeat",
        "repeat": 9,
        "action": {
            "type": "origins:chance",
            "chance": 0.5,
            "action": {
                "type": "origins:give",
                "stack": {
                    "item": "minecraft:gold_nugget",
                    "tag": "{FromPower:1b}"
                }
            }
        }
    }
}
```

Attempts to give the player a custom gold nugget 9 times with a 50% chance of success.
