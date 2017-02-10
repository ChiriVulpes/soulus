package com.kallgirl.souls.common.world;

import com.kallgirl.souls.common.IModItem;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class WorldGenerator implements IWorldGenerator, IModItem {
	private OreVein[] veins;
	public WorldGenerator (OreVein... veins) {
		this.veins = veins;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		for (OreVein vein : veins)
			vein.generate(world, random, chunkX, chunkZ);
	}

	@Override
	public void init () {
		GameRegistry.registerWorldGenerator(this, 0);
	}
}
