package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class Darkmeal extends Item {
	public Darkmeal() {
		super("darkmeal");
		addRecipeShapeless(
			ModObjects.get("darkBoneChunk"),
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}