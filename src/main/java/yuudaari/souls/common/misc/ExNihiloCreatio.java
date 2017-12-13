package yuudaari.souls.common.misc;

import exnihilocreatio.ModBlocks;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.util.ItemInfo;
import net.minecraft.init.Blocks;
import yuudaari.souls.common.ModItems;

public class ExNihiloCreatio {
	public static void init() {
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_NORMAL, 0), 0.1f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_DRY, 0), 0.05f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SAND.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_SCALE, 0), 0.02f, 1);
		
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.DIRT.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_FUNGAL, 0), 0.01f, 1);
		
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.SNOW.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_FROZEN, 0), 0.1f, 1);
		
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_NORMAL, 0), 0.05f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_SCALE, 0), 0.02f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(Blocks.GRAVEL.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_FUNGAL, 0), 0.03f, 1);
		
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_NETHER, 0), 0.05f, 1);
		ExNihiloRegistryManager.SIEVE_REGISTRY.register(ModBlocks.netherrackCrushed.getDefaultState(), new ItemInfo(ModItems.BONE_CHUNK_ENDER, 0), 0.01f, 1);
	}
}