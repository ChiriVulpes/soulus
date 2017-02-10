package com.kallgirl.souls.common.item;

public class EnderIronDust extends Item {
	public EnderIronDust() {
		super("enderIronDust");
		glint = true;
		addRecipeShapeless("dustIron", "dustEnder");
	}
}
