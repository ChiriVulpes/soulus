package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class EnderDust extends Item {
	public EnderDust () {
		super("enderDust");
		glint = true;
		addOreDict("dustEnder");
		addRecipeShapeless(
			ModObjects.get("enderBoneChunk"),
			ModObjects.get("sledgehammer").getItemStack(1, WILDCARD_VALUE)
		);
	}
}