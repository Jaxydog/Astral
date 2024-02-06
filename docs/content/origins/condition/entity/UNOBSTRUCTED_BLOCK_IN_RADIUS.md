# Unobstructed Block in Radius

Checks whether there are a specified number of blocks that both fulfill the specified block condition *and* are
unobstructed by any other blocks within a provided radius relative to the entity's feet.

Type ID: `astral:unobstructed_block_in_radius`

### Fields

| Field             | Type            | Default  | Description                                                                    |
|-------------------|-----------------|----------|--------------------------------------------------------------------------------|
| `block_condition` | Block Condition |          | The block condition to check for.                                              |
| `radius`          | Integer         |          | The radius in which to check for possible blocks.                              |
| `shape`           | String          | `"cube"` | Determines the shape of the radius. Accepts `"cube"`, `"star"`, or `"sphere"`. |
| `comparison`      | Comparison      | `">="`   | How the amount of blocks will be compared to the specified value.              |
| `compare_to`      | Integer         | `1`      | The value to be compared against.                                              |
| `step_size`       | Double          | `0.125`  | The distance to move per-step of the obstruction checking raycast.             |

### Examples

```json
{
    "condition": {
        "type": "astral:unobstructed_block_in_radius",
        "radius": 5,
        "shape": "sphere",
        "block_condition": {
            "type": "origins:block",
            "block": "minecraft:glowstone"
        }
    }
}
```

Checks for a block of unobstructed glowstone within a 5-block sphere of the entity.
