package com.kallgirl.souls.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public interface IModItem extends IModObject {

	default ItemStack getItemStack() {
		if (this instanceof Item) return new ItemStack((Item) this);
		else if (this instanceof Block) return new ItemStack((Block) this);
		else throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}
	default ItemStack getItemStack(Integer count) {
		if (this instanceof Item) return new ItemStack((Item)this, count);
		else if (this instanceof Block) return new ItemStack((Block)this, count);
		else throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}
	default ItemStack getItemStack(Integer count, Integer meta) {
		if (this instanceof Item) return new ItemStack((Item)this, count, meta);
		else if (this instanceof Block) return new ItemStack((Block)this, count, meta);
		else throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}
	default com.kallgirl.souls.common.item.Item castItem () {
		if (this instanceof com.kallgirl.souls.common.item.Item) return (com.kallgirl.souls.common.item.Item)this;
		else throw new IllegalArgumentException("Must be called on a valid Item");
	}
	default com.kallgirl.souls.common.block.Block castBlock () {
		if (this instanceof com.kallgirl.souls.common.block.Block) return (com.kallgirl.souls.common.block.Block)this;
		else throw new IllegalArgumentException("Must be called on a valid Block");
	}

	default void addOreDict(String name) {
		if (this instanceof Block) OreDictionary.registerOre(name, new ItemBlock((Block)this));
		else if (this instanceof Item) OreDictionary.registerOre(name, (Item)this);
		else throw new IllegalArgumentException("Must be called on a valid BlockPane or Item");
	}

	default void addRecipeShapeless(Integer count, Object... items) {
		Recipes.addShapeless(getItemStack(count), items);
	}
	default void addRecipeShapeless(Object... items) {
		Recipes.addShapeless(new ItemStack((Item)this), items);
	}

	default void addRecipeShaped(Integer count, String[] recipe, Object... map) {
		Recipes.addShaped(getItemStack(count), recipe, map);
	}
	default void addRecipeShaped(String[] recipe, Object... map) {
		Recipes.addShaped(getItemStack(), recipe, map);
	}

	default void addFurnaceRecipe(Object item) {
		Recipes.addFurnace(getItemStack(), item);
	}
}