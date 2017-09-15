package yuudaari.souls.common.world;

import yuudaari.souls.common.util.IModObject;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public abstract class WorldGenerator implements IWorldGenerator, IModObject {
	protected OreVein[] veins;

	public String getName() {
		return "world_generator";
	}

	public void setName(String name) {
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		for (OreVein vein : veins)
			vein.generate(world, random, chunkX, chunkZ);
	}

	@Override
	public void init() {
		GameRegistry.registerWorldGenerator(this, 0);
	}
}
