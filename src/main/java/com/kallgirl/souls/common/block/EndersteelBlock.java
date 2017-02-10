package com.kallgirl.souls.common.block;

import com.kallgirl.souls.common.Material;
import com.kallgirl.souls.common.ModObjects;
import com.kallgirl.souls.common.Recipes;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class EndersteelBlock extends Block {
	public EndersteelBlock () {
		super("endersteelBlock", new Material(MapColor.GRASS));
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		addRecipeShaped(
			new String[]{
				"EEE",
				"EEE",
				"EEE"
			},
			'E', ModObjects.get("endersteel")
		);
		Recipes.addShapeless(ModObjects.get("endersteel").getItemStack(9), this);
	}
}
