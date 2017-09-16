package yuudaari.souls.common.world.generators;

import yuudaari.souls.common.ModBlocks;
import yuudaari.souls.common.world.ModGenerator;
import yuudaari.souls.common.world.OreVein;
import net.minecraft.init.Blocks;
import net.minecraft.world.DimensionType;

public class GeneratorFossils extends ModGenerator {
	{
		veins = new OreVein[] { new OreVein(ModBlocks.FOSSIL_DIRT, Blocks.DIRT).setSize(3, 7).setChances(300),
				new OreVein(ModBlocks.FOSSIL_DIRT_ENDER, Blocks.DIRT).setSize(2, 5).setChances(100),
				new OreVein(ModBlocks.FOSSIL_NETHERRACK, Blocks.NETHERRACK).setSize(3, 7).setChances(300)
						.setDimension(DimensionType.NETHER),
				new OreVein(ModBlocks.FOSSIL_NETHERRACK_ENDER, Blocks.NETHERRACK).setSize(2, 5).setChances(10)
						.setDimension(DimensionType.NETHER) };
	}
}
