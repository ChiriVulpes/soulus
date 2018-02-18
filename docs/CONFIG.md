[🡄 Return to Main Readme](../README.md)

## Table of Contents
- [Config Syncing](#config-syncing-)
- [Config Profiles](#config-profiles-)
- [Creatures](#creaturescreaturesjson-)
- [Essences](#essencesessencesjson-)
- [Summoner Replacement](#worldsummoner_replacementreplacementjson-)
- [Ore Veins (Fossils)](#worldveinsveinsjson-)
- [Breeding](#miscbreedingjson-)



## Config Syncing [🡅](#table-of-contents)

Most config settings are server-side, but a few are exclusive to the client. In order to allow these client-side settings to exist and not require the server and client configs to be identical, the server-side configs are synced with each client upon connecting to the server. This synchronisation happens automatically. Using `/soulus reload` to reload the server-side configs will also resynchronise each client.

Currently, only particle counts are client-side only (and some still use the server-side setting regardless). Fixing this is on the todo.



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
- **`no_creatures_no_drops` (default)**: No non-summoned vanilla creatures, and no drops from them, if the creatures existed previously. (Non-summoned creatures currently only exist if you have them enabled, and then disable them afterwards)
- **`no_creatures_no_drops_all_mods`**: No non-summoned creatures (no matter what mod), and no drops from them, if they existed previously.
- **`yes_creatures_no_drops`**: Creatures are all enabled, but non-summoned vanilla creatures won't have drops.
- **`yes_creatures_no_drops_all_mods`**: Creatures are all enabled, but non-summoned creatures (from any mod) won't have drops.
- **`yes_creatures_yes_drops`**: Everything is like normal. All creatures will spawn, and everything will have drops.

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



## `world/summoner_replacement/replacement.json` [🡅](#table-of-contents)

Default profiles:
- **`enabled` (default)**: Spawners will be replaced with Summoners with Soulbooks matching the mobs in the spawner.
- **`enabled_empty`**: Spawners will be replaced with empty Summoners.
- **`disabled`**: Spawners will not be replaced.

The default config (`#enabled`):
```json
{
	"fortress": {
		"*": {
			"type": "blazing",
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
			"type": "spooky",
			"midnight_jewel": true
		}
	},
	"stronghold": {
		"*": {
			"type": "ender",
			"midnight_jewel": true
		}
	},
	"mansion": {
		"*": {
			"type": "earthy",
			"midnight_jewel": true
		}
	}
}
```

The first key is used for the structure the spawner is in, and the second key is used for the creature id in the spawner. You can use `*` to target all creatures, `mod:*` to target all creatures from a mod, and the exact creature id to target just one creature.

As for structures, you can use the `/soulus location` command to get see the structures you're currently in. You can target all structures with `*`.

The `type` you provide to the replacer can be any variant of the summoner. This example shows 5 in use, but there are also the styles `"sorrow"`, and `"madness"`.

If the `midnight_jewel` property is set to true, the replaced spawners will be Summoners with a Soulbook and a Midnight Jewel inside. The Soulbook will match the mob which was being spawned from the spawner. The Midnight Jewel will not drop from a natural Summoner.



## `world/veins/veins.json` [🡅](#table-of-contents)

Default profiles:
- **`enabled` (default)**: Veins will be generated as normal.
- **`disabled`**: There will be no veins generated. This is useful only if you will be reimplementing the veins in another dedicated mod. 

The default config (`#enabled`):

```json
{
	"veins": [
		{
			"block": "soulus:fossil_dirt",
			"replace": "minecraft:dirt",
			"chances": 300,
			"size": {
				"min": 3.0,
				"max": 7.0
			},
			"height": {
				"min": 0.0,
				"max": 255.0
			},
			"dimension": null,
			"biome_types_whitelist": [],
			"biome_types_blacklist": [
				"NETHER",
				"OCEAN",
				"END",
				"VOID"
			]
		}
	]
}
```

The "veins" config is used in Soulus by default for the fossil generation. Instead of working via biomes, it works based on biome types. In the example above, there is only one configured vein, of `soulus:fossil_dirt`, which will spawn in `minecraft:dirt`.

`chances` is how many times the generator should attempt to spawn a vein in the chunk.

`size` is how big the vein should be.

`height` is the min and max heights the vein can spawn at.

`dimension` is the dimension id that the vein should spawn in. You can use `/soulus location` to get this information. Providing `null` means the vein can spawn in any dimension.

`biome_types_whitelist` and `biome_types_blacklist` both use "biome types", which allows the generation to automatically support biomes added by other mods. Again, you can use `/soulus location` to get the biome types if the biome you're currently in.


## `misc/breeding.json` [🡅](#table-of-contents)

Default profiles:
- **`none` (default)**: All animal breeding is disabled.
- **`all`**: All animal breeding is enabled. 

The default config (`#none`):

```json
{
	"*": 0.0
}
```

Each key in the JSON will target a creature or multiple creatures. You can use `*` to target all creatures, `<mod>:*` to target all creatures from one mod (eg: `minecraft:*`), and the entire name to target one specific creature `minecraft:pig`.

The value for each key is the chance that the breeding will be successful. `0.0` = 0% chance, `1.0` = 100% chance.

Example:

```json
{
	"*": 0.0,
	"minecraft:pig": 1.0,
	"twilightforest:*": 0.5
}
```

Everything by default can't breed. Pigs can always breed. The twilight forest mobs will be able to breed 50% of the time.