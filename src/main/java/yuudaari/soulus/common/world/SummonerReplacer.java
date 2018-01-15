package yuudaari.soulus.common.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.config_old.ManualSerializer;
import yuudaari.soulus.common.util.GeneratorName;
import yuudaari.soulus.common.util.Logger;

@Mod.EventBusSubscriber
public class SummonerReplacer {

	public static class SummonerReplacerStructureConfig {

		public static ManualSerializer serializer = new ManualSerializer(SummonerReplacerStructureConfig::serialize, SummonerReplacerStructureConfig::deserialize);

		public static JsonElement serialize (Object obj) {
			JsonObject result = new JsonObject();

			SummonerReplacerStructureConfig config = (SummonerReplacerStructureConfig) obj;
			for (Map.Entry<String, EndersteelType> entry : config.endersteelTypesByCreature.entrySet()) {
				result.add(entry.getKey(), new JsonPrimitive(entry.getValue().getName().toLowerCase()));
			}

			return result;
		}

		public static Object deserialize (JsonElement json, Object current) {
			if (json == null || !(json instanceof JsonObject)) {
				Logger.error("summonerReplacer[structure]", "Must be an object");
				return current;
			}

			JsonObject config = (JsonObject) json;

			SummonerReplacerStructureConfig structureConfig = (SummonerReplacerStructureConfig) current;
			structureConfig.endersteelTypesByCreature.clear();

			for (Map.Entry<String, JsonElement> creatureConfig : config.entrySet()) {
				Optional<EndersteelType> endersteelType = Arrays.asList(EndersteelType.values())
					.stream()
					.filter(type -> type.getName().equalsIgnoreCase(creatureConfig.getValue().getAsString()))
					.findFirst();
				if (endersteelType.isPresent()) {
					structureConfig.endersteelTypesByCreature.put(creatureConfig.getKey(), endersteelType.get());
				}
			}

			return structureConfig;
		}

		public Map<String, EndersteelType> endersteelTypesByCreature = new HashMap<>();

		public SummonerReplacerStructureConfig addCreatureReplacement (String creature, EndersteelType type) {
			endersteelTypesByCreature.put(creature, type);
			return this;
		}
	}

	public static ManualSerializer serializer = new ManualSerializer(SummonerReplacer::serialize, SummonerReplacer::deserialize);

	public static JsonElement serialize (Object obj) {
		JsonObject result = new JsonObject();

		SummonerReplacer config = (SummonerReplacer) obj;
		for (Map.Entry<String, SummonerReplacerStructureConfig> entry : config.structureConfigs.entrySet()) {
			result.add(entry.getKey(), SummonerReplacerStructureConfig.serializer.serialize(entry.getValue()));
		}

		return result;
	}

	public static Object deserialize (JsonElement json, Object current) {
		if (json == null || !(json instanceof JsonObject)) {
			Logger.error("creatures", "Must be an object");
			return current;
		}

		JsonObject config = (JsonObject) json;

		SummonerReplacer summonerReplacer = (SummonerReplacer) current;
		summonerReplacer.structureConfigs.clear();

		for (Map.Entry<String, JsonElement> dimensionConfig : config.entrySet()) {
			summonerReplacer.structureConfigs.put(dimensionConfig
				.getKey(), (SummonerReplacerStructureConfig) SummonerReplacerStructureConfig.serializer
					.deserialize(dimensionConfig.getValue(), new SummonerReplacerStructureConfig()));
		}

		return summonerReplacer;
	}

	public Map<String, SummonerReplacerStructureConfig> structureConfigs = new HashMap<>();
	{
		structureConfigs
			.put("*", new SummonerReplacerStructureConfig().addCreatureReplacement("*", EndersteelType.NORMAL));
	}

	public static SummonerReplacer INSTANCE = new SummonerReplacer();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void populateChunkPost (PopulateChunkEvent.Post event) {
		World world = event.getWorld();
		Chunk chunk = world.getChunkFromChunkCoords(event.getChunkX(), event.getChunkZ());
		IChunkGenerator cg = event.getGenerator();

		Map<BlockPos, TileEntity> teMap = chunk.getTileEntityMap();

		SummonerReplacerStructureConfig defaultStructureConfig = INSTANCE.structureConfigs.get("*");
		if (defaultStructureConfig == null)
			return;

		for (TileEntity te : new ArrayList<>(teMap.values())) {
			Block block = te.getBlockType();
			// Logger.info("found a tile entity " + block.getRegistryName());
			if (block == Blocks.MOB_SPAWNER) {
				BlockPos pos = te.getPos();
				// Logger.info("found a spawner " + pos);
				SummonerReplacerStructureConfig structureConfig = defaultStructureConfig;
				for (Map.Entry<String, SummonerReplacerStructureConfig> structureConfigEntry : INSTANCE.structureConfigs
					.entrySet()) {
					if (cg.isInsideStructure(world, GeneratorName.get(structureConfigEntry.getKey()), pos)) {
						structureConfig = structureConfigEntry.getValue();
					}
				}

				String entityType = getTheIdFromAStupidMobSpawnerTileEntity(te);
				// Logger.info("entity type " + entityType);

				EndersteelType endersteelType = structureConfig.endersteelTypesByCreature.get(entityType);
				if (endersteelType == null) {
					endersteelType = structureConfig.endersteelTypesByCreature
						.get(new ResourceLocation(entityType).getResourceDomain() + ":*");
					if (endersteelType == null) {
						endersteelType = structureConfig.endersteelTypesByCreature.get("*");
						if (endersteelType == null) {
							endersteelType = EndersteelType.NORMAL;
						}
					}
				}

				// Logger.info("endersteel type " + endersteelType);

				world.setBlockState(pos, ModBlocks.SUMMONER.getDefaultState()
					.withProperty(Summoner.VARIANT, endersteelType), 7);
			}
		}
	}

	public static String getTheIdFromAStupidMobSpawnerTileEntity (TileEntity te) {
		if (!(te instanceof TileEntityMobSpawner))
			return null;

		TileEntityMobSpawner mste = (TileEntityMobSpawner) te;
		NBTTagCompound nbt = new NBTTagCompound();
		mste.writeToNBT(nbt);

		NBTTagList taglist = nbt.getTagList("SpawnPotentials", 10);
		NBTBase firstSpawnPotential = taglist.get(0);
		if (!(firstSpawnPotential instanceof NBTTagCompound))
			return null;

		NBTTagCompound firstSpawnPotentialTheRealOne = (NBTTagCompound) firstSpawnPotential;
		NBTTagCompound theActualEntityOhMyGod = firstSpawnPotentialTheRealOne.getCompoundTag("Entity");
		return theActualEntityOhMyGod.getString("id");
	}
}
