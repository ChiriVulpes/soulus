package yuudaari.soulus.common.compat;

import exnihilocreatio.ModBlocks;
import exnihilocreatio.blocks.BlockSieve.MeshType;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.manager.ISieveDefaultRegistryProvider;
import exnihilocreatio.registries.registries.SieveRegistry;
import net.minecraft.init.Blocks;
import yuudaari.soulus.common.registration.ItemRegistry;

public class ExNihiloCreatioRecipes implements ISieveDefaultRegistryProvider {

	@Override
	public void registerRecipeDefaults (SieveRegistry registry) {

		// string

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.1f, MeshType.STRING.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_DRY
			.getItemStack(), 0.05f, MeshType.STRING.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.01f, MeshType.STRING.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.02f, MeshType.STRING.getID());

		// flint

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.2f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_FUNGAL
			.getItemStack(), 0.02f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.01f, MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_DRY
			.getItemStack(), 0.1f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.02f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.05f, MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SNOW.getDefaultState(), ItemRegistry.BONE_CHUNK_FROZEN
			.getItemStack(), 0.1f, MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.05f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.01f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.02f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_FUNGAL
			.getItemStack(), 0.03f, MeshType.FLINT.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed
			.getDefaultState(), ItemRegistry.BONE_CHUNK_NETHER.getItemStack(), 0.05f, MeshType.FLINT.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed
			.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER.getItemStack(), 0.01f, MeshType.FLINT.getID());

		// iron

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.3f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_FUNGAL
			.getItemStack(), 0.02f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.02f, MeshType.IRON.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_DRY
			.getItemStack(), 0.2f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.03f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.1f, MeshType.IRON.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SNOW.getDefaultState(), ItemRegistry.BONE_CHUNK_FROZEN
			.getItemStack(), 0.15f, MeshType.IRON.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.1f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.02f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.04f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_FUNGAL
			.getItemStack(), 0.06f, MeshType.IRON.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed
			.getDefaultState(), ItemRegistry.BONE_CHUNK_NETHER.getItemStack(), 0.1f, MeshType.IRON.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed
			.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER.getItemStack(), 0.02f, MeshType.IRON.getID());

		// diamond

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.4f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_FUNGAL
			.getItemStack(), 0.1f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.05f, MeshType.DIAMOND.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_DRY
			.getItemStack(), 0.3f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.05f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.2f, MeshType.DIAMOND.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SNOW.getDefaultState(), ItemRegistry.BONE_CHUNK_FROZEN
			.getItemStack(), 0.2f, MeshType.DIAMOND.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_NORMAL
			.getItemStack(), 0.2f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER
			.getItemStack(), 0.05f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_SCALE
			.getItemStack(), 0.1f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), ItemRegistry.BONE_CHUNK_FUNGAL
			.getItemStack(), 0.1f, MeshType.DIAMOND.getID());

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed
			.getDefaultState(), ItemRegistry.BONE_CHUNK_NETHER.getItemStack(), 0.2f, MeshType.DIAMOND.getID());
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed
			.getDefaultState(), ItemRegistry.BONE_CHUNK_ENDER.getItemStack(), 0.05f, MeshType.DIAMOND.getID());
	}

	public static void init () {
		ExNihiloRegistryManager.registerSieveDefaultRecipeHandler(new ExNihiloCreatioRecipes());
	}
}
