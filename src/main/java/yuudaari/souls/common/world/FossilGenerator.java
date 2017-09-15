package yuudaari.souls.common.world;

import yuudaari.souls.common.ModObjects;
import net.minecraft.init.Blocks;
import net.minecraft.world.DimensionType;

public class FossilGenerator extends WorldGenerator {
	@Override
	public void init() {
		this.veins = new OreVein[] {
				new OreVein(ModObjects.getBlock("fossil_dirt"), Blocks.DIRT).setSize(3, 7).setChances(300),
				new OreVein(ModObjects.getBlock("fossil_dirt_ender"), Blocks.DIRT).setSize(2, 5).setChances(100),
				new OreVein(ModObjects.getBlock("fossil_netherrack"), Blocks.NETHERRACK).setSize(3, 7).setChances(300)
						.setDimension(DimensionType.NETHER),
				new OreVein(ModObjects.getBlock("fossil_netherrack_ender"), Blocks.NETHERRACK).setSize(2, 5)
						.setChances(10).setDimension(DimensionType.NETHER) };

		super.init();
	}
}
