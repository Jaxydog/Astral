# Moon Phase

Checks for the world's current moon phase.

Type ID: `astral:moon_phase`

### Fields

| Field    | Type                                             | Default    | Description                          |
|----------|--------------------------------------------------|------------|--------------------------------------|
| `phase`  | [Moon Phase](../../data/MOON_PHASE.md)           | *optional* | If set, the moon phase to test for.  |
| `phases` | Array of [Moon Phases](../../data/MOON_PHASE.md) | *optional* | If set, the moon phases to test for. |

### Examples

```json
{
    "entity_condition": {
        "type": "astral:moon_phase",
        "phase": "waxing_gibbous"
    }
}
```

Checks whether the current moon phase is a waxing gibbous.

```json
{
    "entity_condition": {
        "type": "astral:moon_phase",
        "phases": [ "full_moon", "new_moon" ],
        "inverted": true
    }
}
```

Checks whether the current moon phase is *not* a full moon or new moon.
