package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.BoneType;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.util.Recipes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class BoneChunkNormal extends BoneChunk {
	public BoneChunkNormal () {
		super("boneChunkNormal", BoneType.NORMAL);

		Recipes.remove(new ItemStack(Items.DYE, WILDCARD_VALUE, 15));
		addRecipeShapeless(3, Items.BONE);
		Recipes.addShapeless(new ItemStack(Items.DYE, 1, 15),
			this,
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}
