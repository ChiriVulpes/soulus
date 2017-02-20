package com.kallgirl.souls.common.world;

import com.kallgirl.souls.common.ModObjects;
import net.minecraft.init.Blocks;
import net.minecraft.world.DimensionType;

public class FossilGenerator extends WorldGenerator {
	public FossilGenerator() {
		super(
			new OreVein(ModObjects.getBlock("dirtFossil"), Blocks.DIRT)
				.setSize(3, 7).setChances(300),
			new OreVein(ModObjects.getBlock("dirtFossilEnder"), Blocks.DIRT)
				.setSize(2, 5).setChances(100),
			new OreVein(ModObjects.getBlock("netherrackFossil"), Blocks.NETHERRACK)
				.setDimension(DimensionType.NETHER).setSize(3, 7).setChances(300),
			new OreVein(ModObjects.getBlock("netherrackFossilEnder"), Blocks.NETHERRACK)
				.setDimension(DimensionType.NETHER).setSize(2, 5).setChances(100)
		);
	}
}
