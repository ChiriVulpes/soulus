[🡄 Return to Main Readme](../README.md)

## Table of Contents
- [Config Profiles](#config-profiles-)
- [Creatures](#creaturescreaturesjson-)
- [Essences](#essencesessencesjson-)
- [Summoner Replacement](#summoner_replacementreplacementjson-)



## Config Profiles [🡅](#table-of-contents)

All config files can optionally use a feature called "config profiles". A profile is a separate config file that you can use instead of the main file, in order to keep multiple versions of the file and switch between them in an easier way. A few config files also come with multiple default profiles, in order to provide quick and easy defaults for all kinds of players.

To use a profile, replace the content of the file with the following:

```json
{
	"profile": "<profile name>"
}
```

`<profile name>` should be the name of the profile you want to use. It will be mapped to a json file of the same name, with `#<profile name>` at the end. For example:

```json
// misc.json
{
	"profile": "cool_stuff"
}
```

This would map to the file `misc#cool_stuff.json`.



## `creatures/creatures.json` [🡅](#table-of-contents)

Default profiles:
- `no_creatures_no_drops`: All mobs which enter the world from a place other than the Summoner are prevented. No mobs other than summoned mobs have drops. (Non-summoned mobs currently only exist if you have them enabled, and then switch to them disabled afterwards)
- `no_creatures_yes_drops`: Same as previous, except drops are enabled for non-summoned mobs, if they exist.
- `yes_creatures_no_drops`: All mob spawns are enabled, but only summoned mobs have drops.
- `yes_creatures_yes_drops`: All mob spawns are enabled, and all mobs have drops.

```json
{
	"*": {
		"*": {
			"*": {
				"spawn_chance": 0.0,
				"drops": {
					"summoned": {
						"has_xp": true,
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
```
This is a simple configuration, which disables all spawns, from every dimension, in every biome, of every creature. The first asterisk is a wildcard matching all dimensions, while the second asterisk is a wildcard matching all biomes, while the third asterisk is a wildcard matching all creatures.

`spawn_chance` affects only natural spawns. `drops`, however, will affect all mobs. In this config, we see Minecraft's bones being removed from all Skeletons and Wither Skeletons. (Soulus bones are added by default, elsewhere in the config)

`drops` is an object that can take any of three properties, `all`, `summoned`, or `spawned`. The blacklist is used above the whitelist, and `summoned/spawned` is used above `all`. For example, you can whitelist all drops, but disable one specific drop (such as by default is done with Skeletons and Wither Skeletons). Another example is disabling all drops, except for one which is whitelisted only when they're summoned.

If the `drops` object is empty, all drops will be disabled.

For listing drops in `whitelisted_drops` or `blacklisted_drops`, you may use `"*"` for everything, `"minecraft:*"` for everything from minecraft (you can do this with any mod as well), or the exact id of the item.

Here's a more complex example:
```json
{
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
```
In this example, by default, all spawns are disabled, except for all twilight forest creatures in any dimension/biome, and the wither, in any dimension/biome.  
Then, any creatures that spawn in an ocean biome are half as likely to spawn.  
Then there's also an explicit configuration so that if the spawn is in the end, and in any vanilla biome, and the entity is an ender dragon, then it can spawn.
In this example, nothing has drops except the ender dragon, the wither, and everything in an ocean biome. However, it also makes it so that nothing naturally spawned, which is in an ocean biome, will drop any "dye" item.

To know the dimension and biome names of modded biomes, you can use the `/soulus location` command.

#### Notes:

- The default profiles for `creatures/creatures.json` contain long sections which disable vanilla bone drops from skeletons, wither skeletons, and strays. It's not recommended to remove this, as if you do, there will be double bone drops from these mobs. 
- When there are no mobs spawning, or very few, the spawning algorithm works a bit harder than usual to make more spawns, so a `spawn_chance` of `0.5` won't end up being half as many mobs. 



## `essences/essences.json` [🡅](#table-of-contents)

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



## `summoner_replacement/replacement.json` [↑](#table-of-contents)

Default profiles:
- **`enabled` (default)**: Spawners will be replaced with Summoners with Soulbooks matching the mobs in the spawner.
- **`enabled_empty`**: Spawners will be replaced with empty Summoners.
- **`disabled`**: Spawners will not be replaced.

The default config (`#enabled`):
```json
{
	"fortress": {
		"*": {
			"type": "blaze",
			"midnight_jewel": true
		}
	},
	"*": {
		"*": {
			"type": "normal",
			"midnight_jewel": true
		}
	},
	"mineshaft": {
		"*": {
			"type": "stone",
			"midnight_jewel": true
		}
	},
	"stronghold": {
		"*": {
			"type": "end_stone",
			"midnight_jewel": true
		}
	},
	"mansion": {
		"*": {
			"type": "wood",
			"midnight_jewel": true
		}
	}
}
```

The first key is used for the structure the spawner is in, and the second key is used for the creature id in the spawner. You can use `*` to target all creatures, `mod:*` to target all creatures from a mod, and the exact creature id to target just one creature.

As for structures, you can use the `/soulus location` command to get see the structures you're currently in. You can target all structures with `*`.

The `type` you provide to the replacer can be any variant of the summoner. This example shows all 5 in use.

If the `midnight_jewel` property is set to true, the replaced spawners will be Summoners with a Soulbook and a Midnight Jewel inside. The Soulbook will match the mob which was being spawned from the spawner. The Midnight Jewel will not drop from a natural Summoner.