package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class IronDust extends Item {
	public IronDust () {
		super("ironDust");
		addOreDict("dustIron");
		addRecipeShapeless(
			"ingotIron",
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}