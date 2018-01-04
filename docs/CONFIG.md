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
				"minecraft:skeleton": {
					"spawn_chance": 0.0,
					"drops": {
						"all": {
							"whitelisted_drops": [
								"*"
							],
							"blacklisted_drops": [
								"minecraft:bone"
							]
						}
					}
				},
				"minecraft:wither_skeleton": {
					"spawn_chance": 0.0,
					"drops": {
						"all": {
							"whitelisted_drops": [
								"*"
							],
							"blacklisted_drops": [
								"minecraft:bone"
							]
						}
					}
				},
				"*": {
					"spawn_chance": 0.0,
					"drops": {
						"summoned": {
							"whitelisted_drops": [
								"*"
							],
							"blacklisted_drops": []
						}
					}
				}
			}
		}
	}
}
```
This is the default configuration, which disables all spawns, from every dimension, in every biome, of every creature. The first asterisk is a wildcard matching all dimensions, while the second asterisk is a wildcard matching all biomes, while the third asterisk is a wildcard matching all creatures.

`spawn_chance` affects only natural spawns. `drops`, however, will affect all mobs. In this config, we see Minecraft's bones being removed from all Skeletons and Wither Skeletons. (Soulus bones are added by default, elsewhere in the config)

`drops` is an object that can take any of three properties, `all`, `summoned`, or `spawned`. The blacklist is used above the whitelist, and `summoned/spawned` is used above `all`. For example, you can whitelist all drops, but disable one specific drop (such as by default is done with Skeletons and Wither Skeletons). Another example is disabling all drops, except for one which is whitelisted only when they're summoned.

If the `drops` object is empty, all drops will be disabled.

For listing drops in `whitelisted_drops` or `blacklisted_drops`, you may use `"*"` for everything, `"minecraft:*"` for everything from minecraft (you can do this with any mod as well), or the exact id of the item.

Here's a more complex example:
```json
{
	"creatures": {
		"*": {
			"*": {
				"*": {
					"spawn_chance": 0.0,
					"drops": {}
				},
				"twilightforest:*": {
					"spawn_chance": 1.0,
					"drops": {}
				},
				"minecraft:wither": {
					"spawn_chance": 1.0,
					"drops": {
						"all": {
							"whitelisted_drops": [
								"*"
							],
							"blacklisted_drops": []
						}
					}
				}
			},
			"minecraft:ocean": {
				"*": {
					"spawn_chance": 0.5,
					"drops": {
						"all": {
							"whitelisted_drops": [
								"*"
							],
							"blacklisted_drops": []
						},
						"spawned": {
							"whitelisted_drops": [],
							"blacklisted_drops": [
								"minecraft:dye"
							]
						}
					}
				}
			}
		},
		"the_end": {
			"minecraft:*": {
				"minecraft:ender_dragon": {
					"spawn_chance": 1.0,
					"drops": {
						"all": {
							"whitelisted_drops": [
								"*"
							],
							"blacklisted_drops": []
						}
					}
				}
			}
		}
	}
}
```
In this example, by default, all spawns are disabled, except for all twilight forest creatures in any dimension/biome, and the wither, in any dimension/biome.  
Then, any creatures that spawn in an ocean biome are half as likely to spawn.  
Then there's also an explicit configuration so that if the spawn is in the end, and in any vanilla biome, and the entity is an ender dragon, then it can spawn.
In this example, nothing has drops except the ender dragon, the wither, and everything in an ocean biome. However, it also makes it so that nothing naturally spawned, which is in an ocean biome, will drop any "dye" item.

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
