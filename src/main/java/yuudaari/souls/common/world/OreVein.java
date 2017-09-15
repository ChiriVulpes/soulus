package yuudaari.souls.common.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class OreVein {
	final Block block;
	final Block generateIn;
	int minSize = 2;
	int maxSize = 6;
	int minHeight = 0;
	int maxHeight = 255;
	int chances = 4;
	DimensionType dimension = DimensionType.OVERWORLD;

	public OreVein(Block block, Block generateIn) {
		this.block = block;
		this.generateIn = generateIn;
	}

	public OreVein setDimension(DimensionType dimension) {
		this.dimension = dimension;
		return this;
	}

	public OreVein setMinSize(int minSize) {
		this.minSize = minSize;
		return this;
	}

	public OreVein setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		return this;
	}

	public OreVein setSize(int minSize, int maxSize) {
		this.minSize = minSize;
		this.maxSize = maxSize;
		return this;
	}

	public OreVein setChances(int chances) {
		this.chances = chances;
		return this;
	}

	public OreVein setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public OreVein setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public OreVein setHeight(int minHeight, int maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		return this;
	}

	public void generate(World world, Random random, int chunkX, int chunkZ) {
		if (world.provider.getDimensionType() == dimension) {
			int vienSize = minSize + random.nextInt(maxSize - minSize);
			int heightRange = maxHeight - minHeight;
			WorldGenMinable gen = new WorldGenMinable(block.getDefaultState(), vienSize,
					blockstate -> blockstate == generateIn.getDefaultState());
			for (int i = 0; i < chances; i++) {
				int xRand = chunkX * 16 + random.nextInt(16);
				int yRand = random.nextInt(heightRange) + minHeight;
				int zRand = chunkZ * 16 + random.nextInt(16);
				gen.generate(world, random, new BlockPos(xRand, yRand, zRand));
			}
		}
	}
}
