package com.kallgirl.souls.common.item;

import net.minecraft.init.Items;

public class Soulbook extends Item {
	public Soulbook() {
		super("soulbook", 1);
		glint = true;
		addRecipeShapeless("dustEnder", Items.BOOK);
	}
}