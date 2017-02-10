package com.kallgirl.souls.common.block;

import com.kallgirl.souls.common.Material;
import com.kallgirl.souls.common.ModObjects;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Items;

public class BlankSpawner extends Block {
	public BlankSpawner() {
		super("blankSpawner", new Material(MapColor.BLACK).setTransparent());
		setHasItem();
		addRecipeShaped(
			new String[]{
				"BEB",
				"BSB",
				"BEB"
			},
			'B', ModObjects.get("endersteelBars"),
			'E', Items.ENDER_PEARL,
			'S', ModObjects.get("soulbook")
		);
	}
}
