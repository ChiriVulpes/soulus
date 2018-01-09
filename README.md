# SOULUS

### A Minecraft mod. You must sift through the ashes of the dead to restore life to the world.

[Roadmap](https://trello.com/b/DfeJpjGX/soulus)

Soulus is a mod I created because I was sick of building giant mob-spawning boxes in skyblock modpacks.

It adds in custom spawners, called Summoners, which start out incredibly underpowered but can be upgraded to be extremely fast and efficient. The upgrades are incredibly expensive and will likely take a long time to craft in the early game, but everything can be automated with other mods. The mod is also highly configurable, so you can make everything cheap and easy if you want to.

There are no modpack distribution restrictions for Soulus. You may use it in any modpack, with any launcher. However, there ARE general distribution restrictions. You may not provide downloads of Soulus outside of a modpack. To put it in layman's terms, all downloads of Soulus must either be as part of a modpack, through the [Github](https://github.com/Yuudaari/soulus), or through the [Curseforge](https://minecraft.curseforge.com/projects/soulus).

See below for an in-depth list of features in the mod.

## Table of Contents
- [Download](#download-)
- [Previews](#previews-)
- [No Mob Spawning and No Mob Drops](#no-mob-spawning-and-no-mob-drops-)
- [Sledgehammer](#sledgehammer-)
- [Fossils](#fossils-)
- [Bone Chunks and Essences](#bone-chunks-and-essences-)
- [Soulbook](#soulbook-)
- [Endersteel](#endersteel-)
- [Summoner](#summoner-)
	- [Summoner Upgrades](#summoner-upgrades-)
		- [Oscillating Gear](#oscillating-gear-)
		- [Murky Orb](#murky-orb-)
		- [Blood Crystal](#blood-crystal-)
- [Skewer](#skewer-)
- [Bone Drops](#bone-drops-)
- [Ash](#ash-)
- [Dark Endersteel](#dark-endersteel-)
- [Composer](#composer-)
- [Misc](#misc-)
	- [Glue](#glue-)
	- [Bark](#bark-)
	- [Barket](#barket-)
	- [Endersteel Block](#endersteel-block-)
	- [Dark Endersteel Block](#dark-endersteel-block-)
- [Mod Support](#mod-support-)
- [Contributing](#contributing-)


## Download [↑](#table-of-contents)

[Github](https://github.com/Yuudaari/soulus/releases/latest)

[CurseForge](https://minecraft.curseforge.com/projects/soulus)

## Previews [↑](#table-of-contents)

![Items](./preview/items.png)

![Example Farm](./preview/example_farm.png)

![Example Autocrafter](./preview/example_composer.png)

## No Mob Spawning and No Mob Drops [↑](#table-of-contents)

All mob spawning is disabled by default, but you can configure this functionality per dimension, biome, and creature, and even enable/disable drops for each. See [Creatures Config](./docs/CONFIG.md#configcreatures-)

Summoner-summoned mobs are exempt from this config. They will always be summoned, and they will always have drops.

## Sledgehammer [↑](#table-of-contents)

![Sledgehammer](./preview/sledgehammer.png)

The Sledgehammer is used to smash materials. It is only used as a crafting ingredient, and currently only comes in iron. Using it as a weapon and more materials for it are planned.

## Fossils [↑](#table-of-contents)

Fossils generate around the world, different types based on the biome and block. Fossils even generate in the nether! These are the kinds of fossils you can expect to see in the world:

![Dirt Fossils](./preview/fossils_dirt.png)
![Sand Fossils](./preview/fossils_sand.png)
![Netherrack Fossils](./preview/fossils_netherrack.png)

(more fossils coming soon)

When you destroy a fossil, it will drop 4 bone chunks for that kind of fossil.

Fossil veins are configurable (and you can even use the config to generate veins of other kinds of blocks, it's pretty vague). See `config.fossil_veins`

## Bone Chunks and Essences [↑](#table-of-contents)

![Bone Chunks](./preview/bone_chunks.png)

A bone chunk can be crafted with a Sledgehammer to produce bonemeal. Normal bone chunks, dry, frozen, and mossy all produce normal bonemeal, while ender bone chunks produce "ender bonemeal" and blackened bone chunks produce "blackened bonemeal". There is a planned use for blackened bonemeal but it has not yet been implemented.

The main use of bone chunks, however, is to collect Mob Essence! By right clicking with a bone chunk, you break it open and collect the essence inside. The possible Essences produced by each bone type is configurable in the [Essences Config](./docs/CONFIG.md#configessences-), but the defaults are as follows:

| Bone Type | Essences |
| --- | --- |
| Normal | Bat (1), Chicken (8), Cow (8), Skeleton (3), Spider (5), Pig (10), Rabbit (4), Sheep (6), Villager (1), None(5) |
| Dry | Horse (1), Llama (10), Husk (5), Cave Spider (1), None (30) |
| Fungal | Mooshroom (1), Zombie (20), Witch (5), Illager (5), Ocelot (10), Parrot (10), None(20) |
| Frozen | Snowman (1), Stray (1), Wolf (5), Polar Bear (10), None (10) |
| Scale | Silverfish (1), Squid (20), Guardian (1), None (10) |
| Nether | Zombie Pigman (20), Blaze (3), Wither Skeleton (1), Ghast (1), None (10) |
| Ender | Shulker (1), Endermite (15), Creeper (10), Enderman (2), None (20) |

## Soulbook [↑](#table-of-contents)

![Soulbook](./preview/soulbook.png)

You craft a Soulbook using a book and some ender bonemeal/ender dust. Soulbooks are how you can reconstruct souls from their essence.

![Filling a Soulbook 1](./preview/filling_soulbook_1.png)
![Filling a Soulbook 2](./preview/filling_soulbook_2.png)

You can fill a soulbook by crafting it with any number of your chosen type of essence. By default all mob types only require 16 essence to reconstruct their soul. That's two crafts if you craft the soulbook with 8 essence each time.

## Endersteel [↑](#table-of-contents)


1. Crush an iron ingot into iron dust.  
![Iron Dust](./preview/iron_dust.png)

2. Crush an ender bone chunk into ender dust.  
![Ender Dust](./preview/ender_dust.png)

3. Combine iron dust with ender bonemeal, into an ender-iron dust blend.  
![Iron-Iron Dust Blend](./preview/ender_iron_dust_blend.png)

4. Smelt the ender-iron dust blend to get an endersteel ingot!  
![Endersteel](./preview/endersteel.png)

Endersteel can be converted between a block and nuggets. You can craft Endersteel bars from a 3x2 of ingots. The recipe produces 16 bars.

![Crafting Endersteel Bars](./preview/endersteel_bars.png)

Here's what they look like in the world:

![Endersteel Bars Preview](./preview/endersteel_bars_preview.png)

## Summoner [↑](#table-of-contents)

Using 8 Endersteel bars and an ender bonemeal in the center, you can craft an Empty Summoner.

![Empty Summoner](./preview/summoner.png)

You can also find empty summoners in the world, in the places where mob spawners would normally generate. (This replacement can be configured/disabled with [`CONFIG::summoner_replacer`](./docs/CONFIG.md#configsummoner-replacer-), but fair warning: the vanilla mob spawner is unable to spawn entities unless you also change the [Creatures Config](./docs/CONFIG.md#configcreatures-))

If you right click on an empty summoner with a filled soulbook, a summoner is created. By default, summoning a mob takes around 10 minutes, only summons one, and you have to be within 3 blocks to increase the summon percentage. (Eg: you can spend 5 minutes at the summoner, then come back later and complete the summon)

Summoning entities does not obey natural spawn rules. You may summon entities anywhere you like, as long as they fit.

If you right click a summoner, all inserted items of the last type inserted are returned. If you sneak + right click a summoner, all items in the summoner are returned.

If a summoner is receiving a redstone signal it will not be active.

Summoners support comparators, they will output a signal strength of 0 if they are at 0% summoned and 15 if they are at 100% summoned.

## Summoner Upgrades [↑](#table-of-contents)

There are three upgrades for a summoner. An Oscillating Gear increases the speed, a Blood Crystal increases the quantity, and a Murky Orb increases the activation range.

### Oscillating Gear [↑](#table-of-contents)

All bone types can be used to create bone gears of varying types.

![Bone Gear](./preview/bone_gear.png)

Using most bone types you can make a gear and surround it by endersteel to create an oscillating gear.

![Oscillating Gear](./preview/oscillating_gear_1.gif)

Surrounding an ender bone gear in iron creates 2 oscillating gears. 

![Oscillating Gear](./preview/oscillating_gear_2.png)

By default, a summoner can hold 16 oscillating gears, ranging from around 10 minutes per summon to around 30 seconds per summon.

### Murky Orb [↑](#table-of-contents)

Surrounding ender bonemeal with slimeballs (or glue) produces a Strange Sticky Ball. 

![Strange Sticky Ball](./preview/strange_sticky_ball.gif)

Crafting a Strange Sticky Ball with essence of any type puts the essence into it. At 64 essence it becomes a Murky Orb.

![Murky Orb](./preview/filling_murky_orb_1.png)
![Murky Orb](./preview/filling_murky_orb_2.png)

When a Murky Orb has been filled, its stack limit is 16.

By default, a summoner can hold 16 murky orbs, ranging from around a range of 5 blocks to be active to around 70 blocks to be active.

### Blood Crystal [↑](#table-of-contents)


1. Smelting an emerald or soul sand produces a Burnt Emerald.   
![Burnt Emerald](./preview/burnt_emerald_1.png)
![Burnt Emerald](./preview/burnt_emerald_2.png)

2. Crafting a Burnt Emerald (or a Shattered Blood Crystal) with redstone and a slimeball or glue produces a Bloody Emerald.  
![Bloody Emerald](./preview/bloody_emerald.gif)

3. Smelting a Bloody Emerald produces a Blood Crystal.  
![Blood Crystal](./preview/blood_crystal.png)

By default, a Blood Crystal requires 1000 blood. You may collect blood in two ways:

- Pricking yourself. Pricking yourself deals 9 damage to you (4.5 hearts) and gives you the effects hunger and nausea. It adds 90 blood to the blood crystal.
- Killing creatures. It deals 1 damage to entities, and collects 3 blood from each prick.

When a Blood Crystal has been filled, its stack limit is 16.

By default, a summoner can hold 16 Blood Crystals, ranging from 1 entity summoned to around 6 entities summoned, max.

## Skewer [↑](#table-of-contents)

![Skewer](./preview/skewer.png)

The Skewer is the Soulus way of killing creatures. It has two upgrades, and the upgrades can be inserted and removed in the same way as the [Summoner](#summoner-).

Skewers can be placed in any orientation, and spikes will extend outwards when the block receives a redstone signal. If you are at the same height as the spikes when they're facing upwards, you will collide with them and not be able to walk into them, but if you are any higher than that (eg: on a slab, a trapdoor, snow layers, or even a carpet), you will fall into them and begin to take damage. There is currently no collision protection for other orientations of the skewer.

The first upgrade for the Skewer is an unfinished Blood Crystal. As the skewer damages entities, it can collect blood into the blood crystal it stores, based on the damage it deals. By default, for each damage it deals to an entity, there is a 50% chance of receiving 1 blood.

The second upgrade for the Skewer is Nether Quartz. By default, the Skewer only deals 1 damage to each entity when they're not resistant to the damage. The Skewer can hold 256 Nether Quartz. At this point it will deal around 11 damage to entities when they're not resistant to the damage.

Other upgrades for the Skewer are planned.

## Bone Drops [↑](#table-of-contents)

By default, most mobs drop bones matching the bone type their essence comes from. This is configurable in the [Essences Config](./docs/CONFIG.md#configessences-)

## Ash [↑](#table-of-contents)

After collecting Blackened Bonemeal, you may use it on plants! This is a horrible, terrible fertilizer which in fact does not grow the plant, but reduces it to a pile of ash. It's also not very nice to do. Plant murderer.

You can use ash as a replacement for essence in a Murky Orb, or to create Dark Endersteel.

## Dark Endersteel [↑](#table-of-contents)

1. Crush an iron ingot into iron dust.  
![Iron Dust](./preview/iron_dust.png)

2. Crush an ender bone chunk into ender dust.  
![Ender Dust](./preview/ender_dust.png)

3. Combine iron dust with ender dust and ash, into an ash-ender-iron dust blend.  
![Ash-Ender-Iron Dust Blend](./preview/dust_ender_iron_ashen.png)

4. Smelt the ash-ender-iron dust blend to get a dark endersteel ingot!  
![Dark Endersteel](./preview/dark_endersteel.png)

## Composer [↑](#table-of-contents)

The Composer is a block which requires a multiblock structure to be built. It is an autocrafting table, more or less, which is very expensive and slow, but can be upgraded to be very fast. To build a Composer, here's what you will need:

20 [Endersteel Bars](#endersteel-)  
8 Obsidian  
4 [Endersteel Blocks](#endersteel-block-)  
9 Composer Cells:  
![Composer Cell](./preview/composer_cell.png)  
1 Composer:  
![Composer](./preview/composer.png)

Build the structure like this:  
![Composer Structure](./preview/composer_structure.png)

The composer can be on any side, as long as it's in the middle of that side. Multiple composers will not connect to the same structure, however you can use the same bars, obsidian, and endersteel blocks in multiple composers. (potentially building 4 composers for the price of 3! (minus the composer cells and composer, of course))

The orientation of the composer is like a crafting table GUI. When looking at the structure from the composer, the right side is the top row of the crafting grid.

Each composer cell can hold up to 16 of the same item. *This ignores normal stack limitations.* Any container items will be dropped underneath their cells after a composition completes. (You may put another block with an inventory underneath to deposit them into this inventory instead)

The Composer doesn't work quite like the Summoner, in which it would require nearby players to function. Instead, it uses creatures! Each unique creature type nearby makes it go slightly faster. However, each creature type nearby has a 2% chance of "poofing", or being used up by the Composer, every second. (The creature will vanish entirely in a puff of smoke) EG: To keep a Composer running around the clock, you must feed it creatures with Summoner(s).

The Composer can craft any normal or shapeless recipe, and you can add recipes via json (`soulus:composer_shapeless` or `soulus:composer_shaped`). These recipes will work *only* in the Composer, and not in any other table or autocrafter.

The Composer comes with one recipe, currently. The Soul Catalyst. (Currently has no uses, but will be a big part of late-game Soulus soon)

## Misc [↑](#table-of-contents)

### Glue [↑](#table-of-contents)

![Glue](./preview/glue.gif)

Glue is crafted from sugar, bonemeal, and a bucket of water. It can be used anywhere a slimeball can. You can consume glue.

### Bark [↑](#table-of-contents)

Every 100 or so logs produces 8 bark instead of a log. You can use 2 bark and 3 paper to make a book.

### Barket [↑](#table-of-contents)

![Barket](./preview/barket.png)

Make a bucket shape with Bark to get a "Barket". It can only pick up water and will likely break in a matter of seconds. If you let it break, you'll spill your water!

### Endersteel Block [↑](#table-of-contents)

Crafting 9 endersteel bars into a block produces a Block of Endersteel. When placed into the world, you can use it as a clock, by reading its value via a comparator. The value is 0 when the block is receiving no power. When receiving power, it oscillates in a sin-wave from 0-15, at a speed dependent on the power received.

### Dark Endersteel Block [↑](#table-of-contents)

Crafting 9 dark endersteel bars into a block produces a Block of Dark Endersteel. When placed into the world, you can use it as a random number generator, by reading its value via a comparator. The value is 0 when the block is receiving no power. Whenever it receives a new power level, it chooses a random output level 1-15.

## Mod Support [↑](#table-of-contents)

### Ex Nihilo Creatio

Drops are added automatically to Ex Nihilo Creatio Sieves. The default values are not configurable as they can be configured via Ex Nihilo Creatio configs.

### Other Explicitly Supported Mods

Waila (Hwyla)

## Contributing [↑](#table-of-contents)

If you have an error, bug, or have found an oversight please leave an issue about it. I'll try to get to them as fast as I can. If you want to help develop Soulus and already know how to mod, great, make an issue and then a PR if you know what you want to do. If you don't know how to mod, I probably won't have the time to help teach you, but you're welcome to join my [Discord server](https://discord.gg/fwvBfus) and chat/ask questions.

If you have a suggestion you can also leave them as an issue. I will close suggestions that I dislike or are out of scope for the mod.

If you want to support me financially, consider supporting me on [Patreon](https://www.patreon.com/yuudaari)!

[The Soulus License Copyright (c) 2018 Mackenzie "Yuudaari" McClane](./LICENSE.md)

---

The following preview images are of a Summoner, with changes to the config file that allow for more upgrades to be inserted and buffed effects for the upgrades:

![Pigsplosion](./preview/pigsplosion.png)

![Summoning](./preview/summoning.png)

![Aftermath](./preview/aftermath.png)

By default, they don't get *quite* this powerful. ;) 
