package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.ModObjects;

public class Endersteel extends Item {
	public Endersteel() {
		super("endersteel");
		addFurnaceRecipe(ModObjects.get("dustEnderIron"));
	}
}
