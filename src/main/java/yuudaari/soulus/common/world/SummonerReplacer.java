package yuudaari.soulus.common.world;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.block.summoner.Summoner.Upgrade;
import yuudaari.soulus.common.block.summoner.SummonerTileEntity;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssence;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.config.world.summoner_replacement.ConfigReplacement;
import yuudaari.soulus.common.config.world.summoner_replacement.ConfigStructure;
import yuudaari.soulus.common.config.world.summoner_replacement.ConfigSummonerReplacement;
import yuudaari.soulus.common.registration.BlockRegistry;
import yuudaari.soulus.common.util.GeneratorName;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.nbt.NBTHelper;
import yuudaari.soulus.common.util.nbt.NBTObject;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
@ConfigInjected(Soulus.MODID)
public class SummonerReplacer {

	@Inject public static ConfigSummonerReplacement CONFIG;
	@Inject public static ConfigEssences CONFIG_ESSENCES;

	private static long lastReplacementTime = 0;

	@SubscribeEvent
	public static void onTick (TickEvent.PlayerTickEvent event) {
		final long now = System.currentTimeMillis();
		if (now - lastReplacementTime > 10000) {
			lastReplacementTime = now;
			final int chunkx = (int) (event.player.posX) >> 4;
			final int chunkz = (int) (event.player.posZ) >> 4;
			for (int offsetx = -3; offsetx < 3; offsetx++) {
				for (int offsetz = -3; offsetz < 3; offsetz++) {
					replaceSummonersInChunk(event.player.world, chunkx + offsetx, chunkz + offsetz, null);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void populateChunkPost (PopulateChunkEvent.Post event) {
		replaceSummonersInChunk(event.getWorld(), event.getChunkX(), event.getChunkZ(), event.getGenerator());
	}

	private static void replaceSummonersInChunk (World world, int x, int z, IChunkGenerator generator) {
		Chunk chunk = world.getChunkFromChunkCoords(x, z);

		Map<BlockPos, TileEntity> teMap = chunk.getTileEntityMap();
		if (teMap.size() == 0)
			return;

		ConfigStructure defaultStructureConfig = CONFIG.structures.get("*");
		if (defaultStructureConfig == null)
			return;

		for (TileEntity te : new ArrayList<>(teMap.values())) {
			Block block = te.getBlockType();
			// Logger.info("found a tile entity " + block.getRegistryName());
			if (block == Blocks.MOB_SPAWNER) {
				BlockPos pos = te.getPos();
				ConfigStructure structureConfig = defaultStructureConfig;
				for (Map.Entry<String, ConfigStructure> structureConfigEntry : CONFIG.structures
					.entrySet()) {
					if (generator != null && generator.isInsideStructure(world, GeneratorName.get(structureConfigEntry.getKey()), pos)) {
						structureConfig = structureConfigEntry.getValue();
					}
				}

				final String spawnerEntityType = getSpawnerMob(te);

				// each essence type can spawn multiple creatures, so we need to search for one that can spawn this creature
				final Optional<ConfigEssence> essenceConfig = CONFIG_ESSENCES.essences.stream()
					.filter(essence -> essence
						.getSpawnableCreatures()
						.contains(spawnerEntityType))
					.findFirst();

				if (!essenceConfig.isPresent())
					// this spawner's entity type is not spawnable by any essence type, so it can't be replaced
					return;

				final String entityType = essenceConfig.get().essence;

				// Logger.info("found a " + entityType + " spawner at " + pos.getX() + "," + pos.getY() + "," + pos.getZ());

				// first we try to find a matching structure replacement with the exact entity type
				ConfigReplacement replacement = structureConfig.replacementsByCreature.get(entityType);

				// then we try to find if there's a structure replacement for anything in the entity type's mod
				final String entityMod = new ResourceLocation(entityType).getResourceDomain();
				if (replacement == null)
					replacement = structureConfig.replacementsByCreature.get(entityMod + ":*");

				// then we see if there's a catch-all for replacing all spawners
				if (replacement == null)
					replacement = structureConfig.replacementsByCreature.get("*");

				// if we still don't have a replacement config, this spawner isn't configured to be replaced
				if (replacement == null)
					return;

				// Logger.info("replacement type " + replacement.type.getName());

				world.setBlockState(pos, BlockRegistry.SUMMONER.getDefaultState()
					.withProperty(Summoner.VARIANT, replacement.type)
					.withProperty(Summoner.HAS_SOULBOOK, replacement.midnightJewel), 7);

				if (!replacement.midnightJewel)
					return;

				TileEntity nte = world.getTileEntity(pos);
				if (nte == null || !(nte instanceof SummonerTileEntity)) {
					Logger.warn("Unable to insert midnight jewel into replaced summoner");
					return;
				}

				SummonerTileEntity ste = (SummonerTileEntity) nte;
				ste.upgrades.put(Upgrade.CRYSTAL_DARK, 1);
				ste.setEssenceType(entityType);
				ste.onUpdateUpgrades(false);
			}
		}
	}

	public static String getSpawnerMob (final TileEntity te) {
		if (!(te instanceof TileEntityMobSpawner))
			return null;

		return NBTHelper.get(te)
			.<NBTObject>getList("SpawnPotentials")
			.stream()
			.findFirst()
			.map(spawnPotential -> spawnPotential.getObject("Entity").getString("id"))
			.orElse(null);
	}
}
