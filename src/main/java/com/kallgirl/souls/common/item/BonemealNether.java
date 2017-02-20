package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class BonemealNether extends Item {
	public BonemealNether () {
		super("bonemealNether");
		addRecipeShapeless(
			ModObjects.get("boneChunkNether"),
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}