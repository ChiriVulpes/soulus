package yuudaari.souls.common.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public abstract class ModGenerator implements IWorldGenerator {
	private OreVein[] veins;

	public void setVeins(OreVein[] veins) {
		this.veins = veins;
	}

	public OreVein[] getVeins() {
		return veins;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		for (OreVein vein : veins)
			vein.generate(world, random, chunkX, chunkZ);
	}
}
