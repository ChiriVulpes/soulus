package com.kallgirl.souls.common.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Recipes {
	public static void remove(String oreDict) {
		List<ItemStack> items = OreDictionary.getOres(oreDict);
		for (ItemStack item : items) remove(item.getItem());
	}
	public static void remove(ItemStack recipeOutput) {
		remove(recipeOutput.getItem());
	}
	public static void remove(Item recipeOutput) {
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

		Iterator<IRecipe> recipesIterable = recipes.iterator();

		while (recipesIterable.hasNext()) {
			ItemStack is = recipesIterable.next().getRecipeOutput();
			if (is != null && is.getItem() == recipeOutput)
				recipesIterable.remove();
		}
	}
	public static void addShapeless(ItemStack recipeOutput, Object... items) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(recipeOutput, items));
	}
	public static void addShaped(ItemStack recipeOutput, String[] recipe, Object... map) {
		List<Object> args = new ArrayList<>();
		args.addAll(Arrays.asList(recipe));
		args.addAll(Arrays.asList(map));

		GameRegistry.addRecipe(new ShapedOreRecipe(recipeOutput, args.toArray()));
	}
	public static void addFurnace(ItemStack recipeOutput, Object input) {
		if (input instanceof String) addFurnace(recipeOutput, (String)input);
		else if (input instanceof Item) addFurnace(recipeOutput, (Item)input);
		else if (input instanceof ItemStack) addFurnace(recipeOutput, (ItemStack)input);
	}
	public static void addFurnace(ItemStack recipeOutput, String input) {
		OreDictionary.getOres(input).forEach(inputItem ->
			GameRegistry.addSmelting(inputItem, recipeOutput, 0.5F)
		);
	}
	public static void addFurnace(ItemStack recipeOutput, Item input) {
		GameRegistry.addSmelting(input, recipeOutput, 0.5F);
	}
	public static void addFurnace(ItemStack recipeOutput, ItemStack input) {
		GameRegistry.addSmelting(input, recipeOutput, 0.5F);
	}
}
