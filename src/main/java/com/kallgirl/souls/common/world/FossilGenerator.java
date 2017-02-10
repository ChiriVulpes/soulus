package com.kallgirl.souls.common.world;

import com.kallgirl.souls.common.ModObjects;
import net.minecraft.init.Blocks;

public class FossilGenerator extends WorldGenerator {
	public FossilGenerator() {
		super(
			new OreVein(ModObjects.getBlock("dirtFossil"), Blocks.DIRT).setSize(3, 7).setChances(300),
			new OreVein(ModObjects.getBlock("dirtFossilVibrating"), Blocks.DIRT).setSize(2, 5).setChances(100)
		);
	}
}
