# SOULUS

### A Minecraft mod. You must sift through the ashes of the dead to restore life to the world.

Soulus is a mod I created because I was sick of building giant mob-spawning boxes in skyblock modpacks.

It adds in custom spawners, called Summoners, which start out incredibly underpowered but can be upgraded to be extremely fast and efficient. The upgrades are incredibly expensive and will likely take a long time to craft in the early game, but everything can be automated with other mods. The mod is also highly configurable, so you can make everything cheap and easy if you want to.

There are no distribution restrictions for Soulus. You may use it in any modpack, with any launcher.

See below for an in-depth list of features in the mod.

## Previews

The following preview images are of a Summoner with maximum upgrades:

![Pigsplosion](./preview/pigsplosion.png)

![Summoning](./preview/summoning.png)

![Aftermath](./preview/aftermath.png)

## No Mob Spawning

All mob spawning is disabled by default. See `config.spawn_chance`, 0 = no spawns, 1 = all spawns

## Sledgehammer

![Sledgehammer](./preview/sledgehammer.png)

The Sledgehammer is used to smash materials. It is only used as a crafting ingredient, and currently only comes in iron. Using it as a weapon and more materials for it are planned.

## Fossils

Fossils generate around the world, different types based on the biome and block. Fossils even generate in the nether! These are the kinds of fossils you can expect to see in the world:

![Dirt Fossils](./preview/fossils_dirt.png)
![Sand Fossils](./preview/fossils_sand.png)
![Netherrack Fossils](./preview/fossils_netherrack.png)

(more fossils coming soon)

When you destroy a fossil, it will drop 4 bone chunks for that kind of fossil.

Fossil veins are configurable (and you can even use the config to generate veins of other kinds of blocks, it's pretty vague). See `config.fossil_veins`

## Bone Chunks

![Bone Chunks](./preview/bone_chunks.png)

A bone chunk can be crafted with a Sledgehammer to produce bonemeal. Normal bone chunks, dry, frozen, and mossy all produce normal bonemeal, while ender bone chunks produce "ender bonemeal" and necrotic bone chunks produce "necrotic bonemeal". There is a planned use for necrotic bonemeal but it has not yet been implemented.

The main use of bone chunks, however, is to collect Mob Essence! By right clicking with a bone chunk, you break it open and collect the essence inside. The possible Essences produced by each bone type is configurable, but the defaults are as follows:

| Bone Type | Essences |
| --- | --- |
| Normal | Bat (1), Chicken (8), Cow (8), Skeleton (3), Spider (5), Pig (10), Rabbit (4), Sheep (6), Villager (1), None(5) |
| Dry | Horse (1), Llama (10), Husk (5), Cave Spider (1), None (30) |
| Fungal | Mooshroom (1), Zombie (20), Witch (5), Illager (5), Ocelot (10), Parrot (10), None(20) |
| Frozen | Snowman (1), Stray (1), Wolf (5), Polar Bear (10), None (10) |
| Scale | Silverfish (1), Squid (20), Guardian (1), None (10) |
| Nether | Zombie Pigman (20), Blaze (3), Wither Skeleton (1), Ghast (1), None (10) |
| Ender | Shulker (1), Endermite (15), Creeper (10), Enderman (2), None (20) |

## Soulbook

![Soulbook](./preview/soulbook.png)

You craft a Soulbook using a book and some ender bonemeal/ender dust. Soulbooks are how you can reconstruct souls from their essence.

![Filling a Soulbook](./preview/filling_soulbook.png)

You can fill a soulbook by crafting it with any number of your chosen type of essence. By default all mob types only require 16 essence to reconstruct their soul. That's two crafts if you craft the soulbook with 8 essence each time.

## Endersteel

![Crafting Steps for Endersteel](./preview/endersteel.png)

1. Crush an iron ingot into iron dust.
2. Crush an ender bone chunk into ender bonemeal.
3. Combine iron dust with ender bonemeal, into an ender-iron dust blend.
4. Smelt the ender-iron dust blend to get an endersteel ingot!

Endersteel can be converted between a block and nuggets. You can craft Endersteel bars from a 3x2 of ingots. The recipe produces 16 bars.

![Crafting Endersteel Bars](./preview/endersteel_bars.png)

Here's what they look like in the world:

![Endersteel Bars Preview](./preview/endersteel_bars_preview.png)

## Summoner

Using 8 Endersteel bars and an ender bonemeal in the center, you can craft an Empty Summoner.

![Empty Summoner](./preview/empty_summoner.png)

You can also find empty summoners in the world, in the places where mob spawners used to be. (This replacement can be disabled with `config.replace_spawners_with_summoners`, but fair warning: the vanilla mob spawner is unable to spawn entities unless you also change `config.spawn_chance`)

If you right click on an empty summoner with a filled soulbook, a summoner is created. By default, summoning a mob takes around 10 minutes, only summons one, and you have to be within 3 blocks to increase the summon percentage. (Eg: you can spend 5 minutes at the summoner, then come back later and complete the summon)

Summoning entities does not obey natural spawn rules. You may summon entities anywhere you like, as long as they fit.

If you right click a summoner, all inserted items of the last type inserted are returned. If you sneak + right click a summoner, all items in the summoner are returned.

If a summoner is receiving a redstone signal it will not be active.

Summoners support comparators, they will output a signal strength of 0 if they are at 0% summoned and 15 if they are at 100% summoned.

## Summoner Upgrades

There are three upgrades for a summoner. An Oscillating Gear increases the speed, a Blood Crystal increases the quantity, and a Murky Orb increases the activation range.

### Oscillating Gear

All bone types can be used to create bone gears of varying types.

![Bone Gear](./preview/bone_gear.png)

Using most bone types you can make a gear and surround it by endersteel to create an oscillating gear.

![Oscillating Gear](./preview/oscillating_gear_1.png)

Surrounding an ender bone gear in iron creates 2 oscillating gears. 

![Oscillating Gear](./preview/oscillating_gear_2.png)

By default, a summoner can hold 64 oscillating gears, ranging from around 10 minutes per summon to around 10 seconds per summon.

### Murky Orb

Surrounding ender bonemeal with slimeballs (or glue) produces a Strange Sticky Ball. 

![Strange Sticky Ball](./preview/strange_sticky_ball.png)

Crafting a Strange Sticky Ball with essence of any type puts the essence into it. At 64 essence it becomes a Murky Orb.

![Murky Orb](./preview/murky_orb.png)

When a Murky Orb has been filled, its stack limit is 16.

By default, a summoner can hold 64 murky orbs, ranging from around a range of 3 blocks to summon to around 64 blocks to summon.

### Blood Crystal

![Blood Crystal](./preview/blood_crystal.png)

1. Smelting an emerald or soul sand produces a Burnt Emerald. 
2. Crafting a Burnt Emerald with redstone and a slimeball or glue produces a Bloody Emerald.
3. Smelting a Bloody Emerald produces a Blood Crystal.

By default, a Blood Crystal requires 162 blood. You may collect blood in two ways:

- Pricking yourself. Pricking yourself deals 9 damage to you (4.5 hearts) and gives you the effects hunger and nausea. It adds 9 blood to the blood crystal.
- Killing a creature. It deals 1 damage to entities, and collects no blood, until the entity is killed. The death is worth 3 blood.

When a Blood Crystal has been filled, its stack limit is 16.

By default, a summoner can hold 64 Blood Crystals, ranging from 1 entity summoned to 64 entities summoned.

## Misc

### Glue

Glue is crafted from sugar, bonemeal, and a bucket of water. It can be used anywhere a slimeball can. You can consume glue.

### Bark

Every 100 or so logs produces 8 bark instead of a log. You can use 2 bark and 3 paper to make a book.

## Mod Support

### Ex Nihilo Creatio

Drops are added automatically to Ex Nihilo Creatio Sieves. The default values are not configurable as they can be configured via Ex Nihilo Creatio configs.

### Other Explicitly Supported Mods

Waila (Hwyla)

## Contributing

If you have an error, bug, or have found an oversight please leave an issue about it. I'll try to get to them as fast as I can. If you want to help develop Soulus and already know how to mod, great, make an issue and then a PR if you know what you want to do. If you don't know how to mod, I probably won't have the time to help teach you, but you're welcome to join my [Discord server](https://discord.gg/fwvBfus) and chat/ask questions.

If you have a suggestion you can also leave them as an issue. I will close suggestions that I dislike or are out of scope for the mod.

If you want to support me financially, consider supporting me on [Patreon](https://www.patreon.com/yuudaari)!