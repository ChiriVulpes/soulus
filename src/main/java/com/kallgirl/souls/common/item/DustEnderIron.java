package com.kallgirl.souls.common.item;

public class DustEnderIron extends Item {
	public DustEnderIron () {
		super("dustEnderIron");
		glint = true;
		addRecipeShapeless("dustIron", "dustEnder");
	}
}
