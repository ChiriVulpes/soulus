package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class DustEnder extends Item {
	public DustEnder () {
		super("dustEnder");
		glint = true;
		addOreDict("dustEnder");
		addRecipeShapeless(
			ModObjects.get("boneChunkEnder"),
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}