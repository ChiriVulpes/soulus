[🡄 Return to Main Readme](../README.md)

## Table of Contents
- [Composer](#composer-)
  - [Shaped Recipes](#shaped-recipes-)
  - [Shapeless Recipes](#shapeless-recipes-)
  - [Removing Default Recipes](#removing-default-recipes-)



## Composer [🡅](#table-of-contents)

Importing the Composer support:

```cs
import mods.soulus.Composer;
```

Notes, before we begin:
1. All Composer recipes will require a name.
2. All Composer recipes have a time. By default, their time is `1`, or the normal length of a recipe. All vanilla crafting recipes can be crafted in a Composer in this time. Providing `0.5` as this number will make the recipe craft twice as fast. Most Composer-specific recipes should take *longer*, however; Composer specific recipes, as per the lore of the mod, are using the "souls" of mobs as an ingredient to crafting. Therefore, taking longer infers that the recipe takes more souls.
3. Composer recipes are stored in the normal crafting recipe registry. This means that the vanilla CraftTweaker recipe removal will work on them. However, do not use this in conjunction with adding your own Composer recipes of that type, as vanilla recipe removal happens after Composer recipe registration, so your new recipes will be removed.


### Shaped Recipes [🡅](#table-of-contents)

```java
Composer::addShaped(String name, IItemStack stack, IIngredient[][])
Composer::addShaped(String name, IItemStack stack, float time, IIngredient[][])
```

Not providing the "time" argument in the call will set it to the default of `1`.

Here's an example shaped recipe:
```js
Composer.addShaped("soul_catalyst_from_diamonds", <soulus:soul_catalyst>, 2048, [
	[<minecraft:diamond>, <minecraft:diamond>, <minecraft:diamond>],
	[<minecraft:diamond>, <minecraft:diamond_block>, <minecraft:diamond>],
	[<minecraft:diamond>, <minecraft:diamond>, <minecraft:diamond>]
]);
```


### Shapeless Recipes [🡅](#table-of-contents)

```java
Composer::addShapeless(String name, IItemStack stack, IIngredient[])
Composer::addShapeless(String name, IItemStack stack, float time, IIngredient[])
```

Not providing the "time" argument in the call will set it to the default of `1`.

Here's an example shapeless recipe:
```js
Composer.addShapeless("soul_catalyst_from_ground", <soulus:soul_catalyst>, 3725, [
	<minecraft:dirt>, <minecraft:dirt>, <minecraft:stone>
]);
```


### Removing Default Recipes [🡅](#table-of-contents)

```java
Composer::remove(String name)
Composer::remove(IItemStack output)
```

You can remove Composer recipes by their name and by their output.

Example:
```js
Composer.remove(<soulus:soul_catalyst>);
```
