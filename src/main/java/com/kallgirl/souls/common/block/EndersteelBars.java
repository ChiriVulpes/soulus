package com.kallgirl.souls.common.block;

import com.kallgirl.souls.common.Material;
import com.kallgirl.souls.common.ModObjects;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class EndersteelBars extends BlockPane {
	public EndersteelBars () {
		super("endersteelBars", new Material(MapColor.GRASS));
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
		addRecipeShaped(16,
			new String[]{
				"EEE",
				"EEE"
			},
			'E', ModObjects.get("endersteel")
		);
	}
}
