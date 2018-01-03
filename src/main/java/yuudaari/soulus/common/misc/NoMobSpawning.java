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
import yuudaari.soulus.common.misc.NoMobSpawning.DimensionConfig.BiomeConfig;
import yuudaari.soulus.common.misc.NoMobSpawning.DimensionConfig.BiomeConfig.CreatureConfig;
import yuudaari.soulus.common.util.Logger;

@Mod.EventBusSubscriber
public class NoMobSpawning {
	public static NoMobSpawning INSTANCE = new NoMobSpawning();

	public static ManualSerializer serializer = new ManualSerializer(NoMobSpawning::serialize,
			NoMobSpawning::deserialize);

	public static JsonElement serialize(Object obj) {
		JsonObject result = new JsonObject();

		NoMobSpawning config = (NoMobSpawning) obj;
		for (Map.Entry<String, DimensionConfig> entry : config.dimensionConfigs.entrySet()) {
			result.add(entry.getKey(), DimensionConfig.serializer.serialize(entry.getValue()));
		}

		return result;
	}

	public static Object deserialize(JsonElement json, Object current) {
		if (json == null || !(json instanceof JsonObject)) {
			Logger.error("creatures", "Must be an object");
			return current;
		}

		JsonObject config = (JsonObject) json;

		NoMobSpawning noMobSpawning = (NoMobSpawning) current;
		noMobSpawning.dimensionConfigs.clear();

		for (Map.Entry<String, JsonElement> dimensionConfig : config.entrySet()) {
			noMobSpawning.dimensionConfigs.put(dimensionConfig.getKey(), (DimensionConfig) DimensionConfig.serializer
					.deserialize(dimensionConfig.getValue(), new DimensionConfig()));
		}

		return noMobSpawning;
	}

	public static class DimensionConfig {

		public static ManualSerializer serializer = new ManualSerializer(DimensionConfig::serialize,
				DimensionConfig::deserialize);

		public static JsonElement serialize(Object obj) {
			JsonObject result = new JsonObject();

			DimensionConfig config = (DimensionConfig) obj;
			for (Map.Entry<String, BiomeConfig> entry : config.biomeConfigs.entrySet()) {
				result.add(entry.getKey(), BiomeConfig.serializer.serialize(entry.getValue()));
			}

			return result;
		}

		public static Object deserialize(JsonElement json, Object current) {
			if (json == null || !(json instanceof JsonObject)) {
				Logger.error("dimension", "Must be an object");
				return current;
			}

			JsonObject config = (JsonObject) json;

			DimensionConfig dimensionConfig = (DimensionConfig) current;
			dimensionConfig.biomeConfigs.clear();

			for (Map.Entry<String, JsonElement> biomeConfig : config.entrySet()) {
				dimensionConfig.biomeConfigs.put(biomeConfig.getKey(),
						(BiomeConfig) BiomeConfig.serializer.deserialize(biomeConfig.getValue(), new BiomeConfig()));
			}

			return dimensionConfig;
		}

		public static class BiomeConfig {

			public static ManualSerializer serializer = new ManualSerializer(BiomeConfig::serialize,
					BiomeConfig::deserialize);

			public static JsonElement serialize(Object obj) {
				JsonObject result = new JsonObject();

				BiomeConfig config = (BiomeConfig) obj;
				for (Map.Entry<String, CreatureConfig> entry : config.creatureConfigs.entrySet()) {
					result.add(entry.getKey(), CreatureConfig.serializer.serialize(entry.getValue()));
				}

				return result;
			}

			public static Object deserialize(JsonElement json, Object current) {
				if (json == null || !(json instanceof JsonObject)) {
					Logger.error("dimension.biome", "Must be an object");
					return current;
				}

				JsonObject config = (JsonObject) json;

				BiomeConfig biomeConfig = (BiomeConfig) current;
				biomeConfig.creatureConfigs.clear();

				for (Map.Entry<String, JsonElement> creatureConfig : config.entrySet()) {
					biomeConfig.creatureConfigs.put(creatureConfig.getKey(), (CreatureConfig) CreatureConfig.serializer
							.deserialize(creatureConfig.getValue(), new CreatureConfig()));
				}

				return biomeConfig;
			}

			public static class CreatureConfig {

				public static class DropConfig {

					public static Serializer<DropConfig> serializer = new Serializer<>(DropConfig.class);
					static {
						serializer.fieldHandlers.put("whitelistedDrops", new ListSerializer());
						serializer.fieldHandlers.put("blacklistedDrops", new ListSerializer());
					}

					public List<String> whitelistedDrops = new ArrayList<>();
					public List<String> blacklistedDrops = new ArrayList<>();

					public DropConfig() {
					}

					public DropConfig(boolean whitelistAll) {
						if (whitelistAll) {
							whitelistedDrops.add("*");
						}
					}
				}

				public static Serializer<CreatureConfig> serializer = new Serializer<>(CreatureConfig.class,
						"spawnChance");
				static {
					serializer.fieldHandlers.put("drops",
							new ManualSerializer(CreatureConfig::serializeDrops, CreatureConfig::deserializeDrops));
				}

				public static JsonElement serializeDrops(Object obj) {
					JsonObject result = new JsonObject();

					@SuppressWarnings("unchecked")
					Map<String, DropConfig> config = (Map<String, DropConfig>) obj;
					for (Map.Entry<String, DropConfig> entry : config.entrySet()) {
						result.add(entry.getKey(), DropConfig.serializer.serialize(entry.getValue()));
					}

					return result;
				}

				public static Object deserializeDrops(JsonElement json, Object current) {
					if (json == null || !(json instanceof JsonObject)) {
						Logger.error("dimension.biome.drops", "Must be an object");
						return current;
					}

					JsonObject config = (JsonObject) json;

					@SuppressWarnings("unchecked")
					Map<String, DropConfig> drops = (Map<String, DropConfig>) current;
					drops.clear();

					for (Map.Entry<String, JsonElement> dropConfig : config.entrySet()) {
						drops.put(dropConfig.getKey(), (DropConfig) DropConfig.serializer
								.deserialize(dropConfig.getValue(), new DropConfig()));
					}

					return drops;
				}

				public CreatureConfig() {
				}

				public CreatureConfig(double spawnChance) {
					this.spawnChance = spawnChance;
				}

				public String creatureId;
				public double spawnChance = 0;
				public Map<String, DropConfig> drops = new HashMap<>();

				public CreatureConfig setWhitelistedDrops(String spawnType, String... whitelistedDrops) {
					DropConfig dc = drops.get(spawnType);
					if (dc == null)
						drops.put(spawnType, dc = new DropConfig());
					dc.whitelistedDrops = Arrays.asList(whitelistedDrops);
					return this;
				}

				public CreatureConfig setBlacklistedDrops(String spawnType, String... blacklistedDrops) {
					DropConfig dc = drops.get(spawnType);
					if (dc == null)
						drops.put(spawnType, dc = new DropConfig(true));
					dc.blacklistedDrops = Arrays.asList(blacklistedDrops);
					return this;
				}
			}

			public BiomeConfig() {
			}

			public BiomeConfig(Map<String, CreatureConfig> creatureConfigs) {
				this.creatureConfigs = creatureConfigs;
			}

			public String biomeId;
			public Map<String, CreatureConfig> creatureConfigs = new HashMap<>();
		}

		public DimensionConfig() {
		}

		public DimensionConfig(Map<String, BiomeConfig> creatureConfigs) {
			this.biomeConfigs = creatureConfigs;
		}

		public String dimensionId;
		public Map<String, BiomeConfig> biomeConfigs = new HashMap<>();
	}

	public Map<String, DimensionConfig> dimensionConfigs = new HashMap<>();
	{
		Map<String, CreatureConfig> creatureConfigs = new HashMap<>();
		creatureConfigs.put("*", new CreatureConfig(0.0).setWhitelistedDrops("summoned", "*"));
		creatureConfigs.put("minecraft:skeleton", new CreatureConfig(0.0).setBlacklistedDrops("all", "minecraft:bone"));
		creatureConfigs.put("minecraft:wither_skeleton",
				new CreatureConfig(0.0).setBlacklistedDrops("all", "minecraft:bone"));

		Map<String, BiomeConfig> biomeConfigs = new HashMap<>();
		biomeConfigs.put("*", new BiomeConfig(creatureConfigs));

		dimensionConfigs = new HashMap<>();
		dimensionConfigs.put("*", new DimensionConfig(biomeConfigs));
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
		DimensionConfig dimensionConfig = INSTANCE.dimensionConfigs.get(dimension.getName());
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
		BiomeConfig biomeConfig = dimensionConfig.biomeConfigs.get(biome.getRegistryName().toString());
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
		CreatureConfig creatureConfig = biomeConfig.creatureConfigs.get(entityName);
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
