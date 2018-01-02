package yuudaari.soulus.common.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.common.config.ListSerializer;
import yuudaari.soulus.common.config.ManualSerializer;
import yuudaari.soulus.common.config.Serializer;
import yuudaari.soulus.common.misc.NoMobSpawning.NoMobSpawningDimensionConfig.NoMobSpawningBiomeConfig;
import yuudaari.soulus.common.misc.NoMobSpawning.NoMobSpawningDimensionConfig.NoMobSpawningBiomeConfig.NoMobSpawningCreatureConfig;
import yuudaari.soulus.common.util.Logger;

@Mod.EventBusSubscriber
public class NoMobSpawning {
	public static NoMobSpawning INSTANCE = new NoMobSpawning();

	public static ManualSerializer serializer = new ManualSerializer(NoMobSpawning::serialize,
			NoMobSpawning::deserialize);

	public static JsonElement serialize(Object obj) {
		JsonObject result = new JsonObject();

		NoMobSpawning config = (NoMobSpawning) obj;
		for (Map.Entry<String, NoMobSpawningDimensionConfig> entry : config.dimensionConfigs.entrySet()) {
			result.add(entry.getKey(), NoMobSpawningDimensionConfig.serializer.serialize(entry.getValue()));
		}

		return result;
	}

	public static Object deserialize(JsonElement json, Object current) {
		if (json == null || !(json instanceof JsonObject)) {
			Logger.info("creatures", "Must be an object");
			return current;
		}

		JsonObject config = (JsonObject) json;

		NoMobSpawning noMobSpawning = (NoMobSpawning) current;
		noMobSpawning.dimensionConfigs.clear();

		for (Map.Entry<String, JsonElement> dimensionConfig : config.entrySet()) {
			noMobSpawning.dimensionConfigs.put(dimensionConfig.getKey(),
					(NoMobSpawningDimensionConfig) NoMobSpawningDimensionConfig.serializer
							.deserialize(dimensionConfig.getValue(), new NoMobSpawningDimensionConfig()));
		}

		return noMobSpawning;
	}

	public static class NoMobSpawningDimensionConfig {

		public static ManualSerializer serializer = new ManualSerializer(NoMobSpawningDimensionConfig::serialize,
				NoMobSpawningDimensionConfig::deserialize);

		public static JsonElement serialize(Object obj) {
			JsonObject result = new JsonObject();

			NoMobSpawningDimensionConfig config = (NoMobSpawningDimensionConfig) obj;
			for (Map.Entry<String, NoMobSpawningBiomeConfig> entry : config.biomeConfigs.entrySet()) {
				result.add(entry.getKey(), NoMobSpawningBiomeConfig.serializer.serialize(entry.getValue()));
			}

			return result;
		}

		public static Object deserialize(JsonElement json, Object current) {
			if (json == null || !(json instanceof JsonObject)) {
				Logger.info("creatures[dimension]", "Must be an object");
				return current;
			}

			JsonObject config = (JsonObject) json;

			NoMobSpawningDimensionConfig dimensionConfig = (NoMobSpawningDimensionConfig) current;
			dimensionConfig.biomeConfigs.clear();

			for (Map.Entry<String, JsonElement> biomeConfig : config.entrySet()) {
				dimensionConfig.biomeConfigs.put(biomeConfig.getKey(),
						(NoMobSpawningBiomeConfig) NoMobSpawningBiomeConfig.serializer
								.deserialize(biomeConfig.getValue(), new NoMobSpawningBiomeConfig()));
			}

			return dimensionConfig;
		}

		public static class NoMobSpawningBiomeConfig {

			public static ManualSerializer serializer = new ManualSerializer(NoMobSpawningBiomeConfig::serialize,
					NoMobSpawningBiomeConfig::deserialize);

			public static JsonElement serialize(Object obj) {
				JsonObject result = new JsonObject();

				NoMobSpawningBiomeConfig config = (NoMobSpawningBiomeConfig) obj;
				for (Map.Entry<String, NoMobSpawningCreatureConfig> entry : config.creatureConfigs.entrySet()) {
					result.add(entry.getKey(), NoMobSpawningCreatureConfig.serializer.serialize(entry.getValue()));
				}

				return result;
			}

			public static Object deserialize(JsonElement json, Object current) {
				if (json == null || !(json instanceof JsonObject)) {
					Logger.info("creatures[dimension][biome]", "Must be an object");
					return current;
				}

				JsonObject config = (JsonObject) json;

				NoMobSpawningBiomeConfig biomeConfig = (NoMobSpawningBiomeConfig) current;
				biomeConfig.creatureConfigs.clear();

				for (Map.Entry<String, JsonElement> creatureConfig : config.entrySet()) {
					biomeConfig.creatureConfigs.put(creatureConfig.getKey(),
							(NoMobSpawningCreatureConfig) NoMobSpawningCreatureConfig.serializer
									.deserialize(creatureConfig.getValue(), new NoMobSpawningCreatureConfig()));
				}

				return biomeConfig;
			}

			public static class NoMobSpawningCreatureConfig {
				public static Serializer<NoMobSpawningCreatureConfig> serializer = new Serializer<>(
						NoMobSpawningCreatureConfig.class, "spawnChance");
				static {
					serializer.fieldHandlers.put("whitelistedDrops", new ListSerializer());
					serializer.fieldHandlers.put("blacklistedDrops", new ListSerializer());
				}

				public NoMobSpawningCreatureConfig() {
				}

				public NoMobSpawningCreatureConfig(double spawnChance) {
					this.spawnChance = spawnChance;
				}

				public String creatureId;
				public double spawnChance = 0;
				public List<String> whitelistedDrops = new ArrayList<>(Arrays.asList("*"));
				public List<String> blacklistedDrops = new ArrayList<>();

				public NoMobSpawningCreatureConfig setWhitelistedDrops(String... whitelistedDrops) {
					this.whitelistedDrops = Arrays.asList(whitelistedDrops);
					return this;
				}

				public NoMobSpawningCreatureConfig setBlacklistedDrops(String... blacklistedDrops) {
					this.blacklistedDrops = Arrays.asList(blacklistedDrops);
					return this;
				}
			}

			public NoMobSpawningBiomeConfig() {
			}

			public NoMobSpawningBiomeConfig(Map<String, NoMobSpawningCreatureConfig> creatureConfigs) {
				this.creatureConfigs = creatureConfigs;
			}

			public String biomeId;
			public Map<String, NoMobSpawningCreatureConfig> creatureConfigs = new HashMap<>();
		}

		public NoMobSpawningDimensionConfig() {
		}

		public NoMobSpawningDimensionConfig(Map<String, NoMobSpawningBiomeConfig> creatureConfigs) {
			this.biomeConfigs = creatureConfigs;
		}

		public String dimensionId;
		public Map<String, NoMobSpawningBiomeConfig> biomeConfigs = new HashMap<>();
	}

	public Map<String, NoMobSpawningDimensionConfig> dimensionConfigs = new HashMap<>();
	{
		Map<String, NoMobSpawningCreatureConfig> creatureConfigs = new HashMap<>();
		creatureConfigs.put("*", new NoMobSpawningCreatureConfig(0.0));
		creatureConfigs.put("minecraft:skeleton",
				new NoMobSpawningCreatureConfig(0.0).setBlacklistedDrops("minecraft:bone"));
		creatureConfigs.put("minecraft:wither_skeleton",
				new NoMobSpawningCreatureConfig(0.0).setBlacklistedDrops("minecraft:bone"));

		Map<String, NoMobSpawningBiomeConfig> biomeConfigs = new HashMap<>();
		biomeConfigs.put("*", new NoMobSpawningBiomeConfig(creatureConfigs));

		dimensionConfigs = new HashMap<>();
		dimensionConfigs.put("*", new NoMobSpawningDimensionConfig(biomeConfigs));
	}

	@SubscribeEvent
	public static void onMobJoinWorld(EntityJoinWorldEvent event) {

		// first we check if we should even try to cancel the spawn
		Entity entity = event.getEntity();
		if (entity == null || !(entity instanceof EntityLiving) || event.getWorld().isRemote)
			return;

		// then we check if the creature has already been whitelisted
		NBTTagCompound entityData = entity.getEntityData();
		if (entityData.hasKey("soulus:spawn_whitelisted", 1))
			return;

		// then we get the dimension config for this potential spawn
		DimensionType dimension = event.getWorld().provider.getDimensionType();
		//Logger.info(dimension.getName());
		NoMobSpawningDimensionConfig dimensionConfig = INSTANCE.dimensionConfigs.get(dimension.getName());
		if (dimensionConfig == null) {
			dimensionConfig = INSTANCE.dimensionConfigs.get("*");
			if (dimensionConfig == null) {
				approveSpawn(entity);
				return;
			}
		}

		// then we get the biome config for this potential spawn
		BlockPos pos = entity.getPosition();
		Biome biome = event.getWorld().getBiome(pos);
		//Logger.info(biome.getRegistryName().toString());
		NoMobSpawningBiomeConfig biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().toString());
		if (biomeConfig == null) {
			biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().getResourceDomain() + ":*");
			if (biomeConfig == null) {
				biomeConfig = dimensionConfig.biomeConfigs.get("*");
				if (biomeConfig == null) {
					approveSpawn(entity);
					return;
				}
			}
		}

		// then we get the creature config for this potential spawn
		String entityName = EntityList.getKey(entity).toString();
		//Logger.info(entityName);
		NoMobSpawningCreatureConfig creatureConfig = biomeConfig.creatureConfigs.get(entityName);
		if (creatureConfig == null) {
			creatureConfig = biomeConfig.creatureConfigs
					.get(new ResourceLocation(entityName).getResourceDomain() + ":*");
			if (creatureConfig == null) {
				creatureConfig = biomeConfig.creatureConfigs.get("*");
				if (creatureConfig == null) {
					approveSpawn(entity);
					return;
				}
			}
		}

		// if we have 100% spawn chance, don't attempt to cancel
		if (creatureConfig.spawnChance == 1) {
			approveSpawn(entity);
			return;
		}

		// if we have 0% spawn chance, cancel, otherwise, randomly decide based on the spawn chance
		if (creatureConfig.spawnChance == 0 || event.getWorld().rand.nextDouble() >= creatureConfig.spawnChance) {
			event.setCanceled(true);
		}
	}

	private static void approveSpawn(Entity entity) {
		entity.getEntityData().setByte("soulus:spawn_whitelisted", (byte) 1);
	}
}
