package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class DustIron extends Item {
	public DustIron () {
		super("dustIron");
		addOreDict("dustIron");
		addRecipeShapeless(
			"ingotIron",
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}