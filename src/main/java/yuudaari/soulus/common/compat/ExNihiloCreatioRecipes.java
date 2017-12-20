package yuudaari.soulus.common.compat;

import exnihilocreatio.ModBlocks;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.manager.ISieveDefaultRegistryProvider;
import exnihilocreatio.registries.registries.SieveRegistry;
import exnihilocreatio.util.ItemInfo;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Loader;
import yuudaari.soulus.common.ModItems;

public class ExNihiloCreatioRecipes implements ISieveDefaultRegistryProvider {

	@Override
	public void registerRecipeDefaults(SieveRegistry registry) {

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_NORMAL, 0), 0.2f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_FUNGAL, 0), 0.01f, 2);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 2);

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_DRY, 0), 0.05f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_SCALE, 0), 0.02f, 1);

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SNOW.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_FROZEN, 0), 0.1f, 2);

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_NORMAL, 0), 0.05f, 2);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 2);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_SCALE, 0), 0.02f, 2);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_FUNGAL, 0), 0.03f, 2);

		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_NETHER, 0), 0.05f, 2);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed.getDefaultState(),
				new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 2);
	}

	public static void init() {
		if (!Loader.isModLoaded("exnihilocreatio")) {
			return;
		}

		ExNihiloRegistryManager.registerSieveDefaultRecipeHandler(new ExNihiloCreatioRecipes());
	}
}