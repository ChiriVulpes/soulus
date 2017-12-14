package yuudaari.souls.common.misc;

import exnihilocreatio.ModBlocks;
import exnihilocreatio.blocks.BlockSieve;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Loader;
import yuudaari.souls.common.ModItems;

public class ExNihiloCreatio {
	public static void init() {
		if (!Loader.isModLoaded("exnihilocreatio")) {
			return;
		}

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				ModItems.BONE_CHUNK_NORMAL.getItemStack(), 0.1f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				ModItems.BONE_CHUNK_DRY.getItemStack(), 0.05f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				ModItems.BONE_CHUNK_ENDER.getItemStack(), 0.01f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				ModItems.BONE_CHUNK_SCALE.getItemStack(), 0.02f, BlockSieve.MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(),
				ModItems.BONE_CHUNK_FUNGAL.getItemStack(), 0.01f, BlockSieve.MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SNOW.getDefaultState(),
				ModItems.BONE_CHUNK_FROZEN.getItemStack(), 0.1f, BlockSieve.MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				ModItems.BONE_CHUNK_NORMAL.getItemStack(), 0.05f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				ModItems.BONE_CHUNK_ENDER.getItemStack(), 0.01f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				ModItems.BONE_CHUNK_SCALE.getItemStack(), 0.02f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				ModItems.BONE_CHUNK_FUNGAL.getItemStack(), 0.03f, BlockSieve.MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed.getDefaultState(),
				ModItems.BONE_CHUNK_NETHER.getItemStack(), 0.05f, BlockSieve.MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed.getDefaultState(),
				ModItems.BONE_CHUNK_ENDER.getItemStack(), 0.01f, BlockSieve.MeshType.FLINT.getID());
	}
}