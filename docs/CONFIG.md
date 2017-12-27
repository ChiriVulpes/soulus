[<- Return to Main Page](../README.md)

## Table of Contents
- [Creatures](#configcreatures-)
- [Essences](#configessences-)

## `CONFIG::creatures` [↑](#table-of-contents)

```json
{
	"creatures": {
		"*": {
			"*": {
				"*": {
					"spawn_chance": 0.0,
					"has_drops": true
				}
			}
		}
	}
}
```
This is the default configuration, which disables all spawns, from every dimension, in every biome, of every creature. The first asterisk is a wildcard matching all dimensions, while the second asterisk is a wildcard matching all biomes, while the third asterisk is a wildcard matching all creatures.

If you configure any creatures to still spawn, by default, they will all have drops, as seen in this config.

Here's a more complex example:
```json
{
	"creatures": {
		"*": {
			"*": {
				"*": {
					"spawn_chance": 0.0,
					"has_drops": false
				},
				"twilightforest:*": {
					"spawn_chance": 1.0,
					"has_drops": false
				},
				"minecraft:wither": {
					"spawn_chance": 1.0,
					"has_drops": true
				}
			},
			"minecraft:ocean": {
				"*": {
					"spawn_chance": 0.5,
					"has_drops": true
				}
			}
		},
		"the_end": {
			"minecraft:*": {
				"minecraft:ender_dragon": {
					"spawn_chance": 1.0,
					"has_drops": true
				}
			}
		}
	}
}
```
In this example, by default, all spawns are disabled, except for all twilight forest creatures in any dimension/biome, and the wither, in any dimension/biome.  
Then, any creatures that spawn in an ocean biome are half as likely to spawn.  
Then there's also an explicit configuration so that if the spawn is in the end, and in any vanilla biome, and the entity is an ender dragon, then it can spawn.
In this example, nothing has drops except the ender dragon, the wither, and everything in an ocean biome.

To know the dimension and biome names of modded biomes, you can use the /souluslocation command.

  
As a side-note, when there are no mobs spawning, or very few, the spawning algorithm works a bit harder than usual to make more spawns, so a `spawn_chance` of 0.5 won't end up being half as many mobs. 

## `CONFIG::essences` [↑](#table-of-contents)

```json
{
	"essences": [
		{
			"essence": "minecraft:zombie",
			"soulbook_quantity": 16,
			"bones": {
				"drop_weight": 20.0,
				"type": "fungal"
			},
			"spawns": {
				"minecraft:zombie_horse": 1.0,
				"minecraft:zombie": 100.0,
				"minecraft:zombie_villager": 10.0
			},
			"loot": {
				"minecraft:zombie_horse": {
					"min": 2,
					"max": 5,
					"chance": 1.0
				},
				"minecraft:zombie": {
					"min": 1,
					"max": 3,
					"chance": 0.5
				},
				"minecraft:zombie_villager": {
					"min": 1,
					"max": 4,
					"chance": 0.8
				}
			}
		}
	]
}
```

`essences` is an array of `CreatureConfig` objects representing creatures that can be collected from bone chunks and summoned from summoners. The following is a description of each property in the `CreatureConfig` object:

`essence`: The ResourceLocation of the mob that the essence name and colours use.  
`soulbook_quantity`: The number of these essences that a soulbook needs to be filled and summon this creature.  
`bones`: An object containing the `type`, which is the bone type (eg, `normal`, `frozen`, `fungal`, `nether`, etc), and the `drop_weight` which is how frequent this essence type drops compared to the other essences in the given bone chunk.  
`spawns`: An object containing each mob that can *actually* spawn from this essence type, and the chance of spawning each over the others. If the object is empty, it can only spawn the mob that `essence` represents. Otherwise, it chooses from `spawns` (it does not include the mob in `essence` unless you specify it!)  
`loot`: An object representing the bone drops for the spawned entities. `min` and `max` are used for the number of bones to drop, and `chance` is the chance that bones should be dropped at all.