# Distance

Compares the entity's current position against a specified target position.

This was implemented because the
[Origins implementation](https://origins.readthedocs.io/en/1.10.0/types/entity_condition_types/distance_from_coordinates/)
caused a server crash.

Type ID: `astral:distance`

### Fields

| Field        | Type             | Default | Description                                                                |
|--------------|------------------|---------|----------------------------------------------------------------------------|
| `position`   | Array of Doubles |         | A list of 3 numbers representing a position.                               |
| `comparison` | Comparison       |         | Determines how the calculated distance is compared to the specified value. |
| `compare_to` | Double           |         | The value to compare the calculated distance to.                           |

### Examples

```json
{
    "condition": {
        "type": "astral:distance",
        "position": [ 256, 64, 32 ],
        "comparison": "<",
        "compare_to": 8
    }
}
```

Checks if the current entity position is within 8 blocks of the coordinates X: 256, Y: 64, Z: 32.

```json
{
    "condition": {
        "type": "astral:distance",
        "position": [ 0, 63, 0 ],
        "comparison": "<",
        "compare_to": 4
    }
}
```

Checks if the current entity position is within 4 blocks of sea level at X: 0, Y: 0.
