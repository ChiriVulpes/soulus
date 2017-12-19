package yuudaari.soulus.common.config;

import yuudaari.soulus.common.ModGenerators;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.block.summoner.SummonerTileEntity;
import yuudaari.soulus.common.item.BloodCrystal;
import yuudaari.soulus.common.item.Glue;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.item.Sledgehammer;
import yuudaari.soulus.common.misc.BarkFromLogs;
import yuudaari.soulus.common.misc.NoMobSpawning;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.world.generators.GeneratorFossils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class Config {

	private static final String configFileName = "soulus.json";

	public Map<BoneType, Map<String, EssenceDropConfig>> drops = EssenceDropConfig.getDefaultDropMap();
	public Map<String, SoulConfig> souls = SoulConfig.getDefaultSoulMap();
	public boolean replaceSpawnersWithSummoners = true;
	public int boneChunkParticleCount = 3;

	/* SERIALIZER */

	private static final Serializer<Config> serializer;
	static {
		serializer = new Serializer<>(Config.class, "spawnChance", "replaceSpawnersWithSummoners",
				"boneChunkParticleCount");

		serializer.fieldHandlers.put("drops", new ManualSerializer(Config::serializeDrops, Config::deserializeDrops));
		serializer.fieldHandlers.put("souls", new ManualSerializer(Config::serializeSouls, Config::deserializeSouls));

		serializer.otherHandlers.put("fossilVeins",
				new ManualSerializer(from -> GeneratorFossils.serialize(ModGenerators.GENERATOR_FOSSILS),
						(from, into) -> GeneratorFossils.deserialize(from, ModGenerators.GENERATOR_FOSSILS)));

		serializer.otherHandlers.put("bloodCrystal",
				new ManualSerializer(from -> BloodCrystal.serializer.serialize(ModItems.BLOOD_CRYSTAL),
						(from, into) -> BloodCrystal.serializer.deserialize(from, ModItems.BLOOD_CRYSTAL)));

		serializer.otherHandlers.put("murkyOrb",
				new ManualSerializer(from -> OrbMurky.serializer.serialize(ModItems.ORB_MURKY),
						(from, into) -> OrbMurky.serializer.deserialize(from, ModItems.ORB_MURKY)));

		serializer.otherHandlers.put("glue", new ManualSerializer(from -> Glue.serializer.serialize(ModItems.GLUE),
				(from, into) -> Glue.serializer.deserialize(from, ModItems.GLUE)));

		serializer.otherHandlers.put("sledgehammer",
				new ManualSerializer(from -> Sledgehammer.serializer.serialize(ModItems.SLEDGEHAMMER),
						(from, into) -> Sledgehammer.serializer.deserialize(from, ModItems.SLEDGEHAMMER)));

		serializer.otherHandlers.put("summoner", new ManualSerializer(from -> SummonerTileEntity.serialize(),
				(from, into) -> SummonerTileEntity.deserialize(from)));

		serializer.otherHandlers.put("barkFromLogs",
				new ManualSerializer(from -> BarkFromLogs.serializer.serialize(BarkFromLogs.INSTANCE),
						(from, into) -> BarkFromLogs.serializer.deserialize(from, BarkFromLogs.INSTANCE)));

		serializer.otherHandlers.put("noMobSpawning",
				new ManualSerializer(from -> NoMobSpawning.serializer.serialize(NoMobSpawning.INSTANCE),
						(from, into) -> NoMobSpawning.serializer.deserialize(from, NoMobSpawning.INSTANCE)));

	}

	/* STATIC METHODS */

	public static Config loadConfig(String configDirectory) {
		Config config = new Config();

		File configFile = new File(configDirectory + "/soulus/" + configFileName);

		if (!configFile.exists()) {
			try {
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			} catch (IOException e) {
				// if we error, we don't worry about the file and instead just use the base configs
				Logger.warn("Could not create a configuration file! Using base settings. Error: " + e.getMessage());
				return config;
			}
		} else {
			JsonElement configJson;
			try {
				configJson = new JsonParser().parse(new FileReader(configFile));
			} catch (IOException | JsonParseException e) {
				Logger.warn("Could not read the configuration file! Using base settings. Error: " + e.getMessage());
				return config;
			}

			serializer.deserialize(configJson, config);
		}

		try {
			Writer writer = new FileWriter(configFile);

			Gson json = new GsonBuilder().create();
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("\t");
			json.toJson(serializer.serialize(config), jsonWriter);

			writer.close();
		} catch (IOException e) {
			Logger.warn("Can't write configuration settings to file.");
		}

		return config;
	}

	/* SERIALIZERS */

	private static JsonElement serializeSouls(Object obj) {
		@SuppressWarnings("unchecked")
		Map<String, SoulConfig> soulMap = (Map<String, SoulConfig>) obj;

		JsonObject souls = new JsonObject();

		for (Map.Entry<String, SoulConfig> soulEntry : soulMap.entrySet()) {
			souls.add(soulEntry.getKey(), SoulConfig.serializer.serialize(soulEntry.getValue()));
		}

		return souls;
	}

	private static Map<String, SoulConfig> deserializeSouls(JsonElement soulMapElement, Object currentObject) {
		@SuppressWarnings("unchecked")
		Map<String, SoulConfig> soulMap = (Map<String, SoulConfig>) currentObject;

		if (soulMapElement == null || !soulMapElement.isJsonObject()) {
			Logger.warn("Config must have key 'souls' which is an object");
			return soulMap;
		}
		JsonObject soulMapJson = soulMapElement.getAsJsonObject();

		for (Map.Entry<String, JsonElement> soulEntry : soulMapJson.entrySet()) {
			String entityType = soulEntry.getKey();
			JsonElement soulElement = soulEntry.getValue();
			if (!soulElement.isJsonObject()) {
				Logger.warn("Soul info for '" + entityType + "' must be an object");
				continue;
			}

			SoulConfig result = SoulConfig.serializer.deserialize(soulElement.getAsJsonObject());
			if (result != null)
				soulMap.put(entityType, result);
		}

		return soulMap;
	}

	private static JsonElement serializeDrops(Object obj) {
		@SuppressWarnings("unchecked")
		Map<BoneType, Map<String, EssenceDropConfig>> dropMap = (Map<BoneType, Map<String, EssenceDropConfig>>) obj;

		JsonObject boneMap = new JsonObject();

		for (Map.Entry<BoneType, Map<String, EssenceDropConfig>> boneEntry : dropMap.entrySet()) {
			JsonObject drops = new JsonObject();
			for (Map.Entry<String, EssenceDropConfig> dropEntry : boneEntry.getValue().entrySet()) {
				drops.add(dropEntry.getKey(), EssenceDropConfig.serializer.serialize(dropEntry.getValue()));
			}

			boneMap.add(BoneType.getString(boneEntry.getKey()), drops);
		}

		return boneMap;
	}

	private static Map<BoneType, Map<String, EssenceDropConfig>> deserializeDrops(JsonElement boneMapElement,
			Object currentObject) {
		@SuppressWarnings("unchecked")
		Map<BoneType, Map<String, EssenceDropConfig>> dropMap = (Map<BoneType, Map<String, EssenceDropConfig>>) currentObject;

		if (boneMapElement == null || !boneMapElement.isJsonObject()) {
			Logger.warn("Config must have key 'drops' which is an object");
			return dropMap;
		}
		JsonObject boneMap = boneMapElement.getAsJsonObject();

		for (Map.Entry<String, JsonElement> boneEntry : boneMap.entrySet()) {
			String boneTypeName = boneEntry.getKey();
			BoneType boneType = BoneType.getBoneType(boneTypeName);
			if (boneType == null) {
				Logger.warn("Invalid boneType '" + boneTypeName + "'");
				continue;
			}
			JsonElement dropMapElement = boneEntry.getValue();
			if (!dropMapElement.isJsonObject()) {
				Logger.warn("BoneType map '" + boneTypeName + "' must be an object");
				continue;
			}
			JsonObject dropMapJson = dropMapElement.getAsJsonObject();
			for (Map.Entry<String, JsonElement> dropConfigEntry : dropMapJson.entrySet()) {
				String entityType = dropConfigEntry.getKey();
				JsonElement dropConfigElement = dropConfigEntry.getValue();
				if (!dropConfigElement.isJsonObject()) {
					Logger.warn("Drop info for '" + boneTypeName + "." + entityType + "' must be an object");
					continue;
				}

				EssenceDropConfig deserialized = EssenceDropConfig.serializer
						.deserialize(dropConfigElement.getAsJsonObject());
				if (deserialized != null)
					dropMap.get(boneType).put(entityType, deserialized);
			}
		}

		return dropMap;
	}

	public static JsonElement serializeList(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) obj;

		JsonArray result = new JsonArray();
		for (String item : list)
			result.add(item);

		return result;
	}

	public static List<String> deserializeList(JsonElement listElement, Object currentObject) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) currentObject;

		if (listElement == null || !listElement.isJsonArray()) {
			Logger.warn("Must be a list of strings");
			return list;
		}
		JsonArray jsonList = listElement.getAsJsonArray();

		list.clear();

		for (JsonElement item : jsonList) {
			if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
				Logger.warn("Must contain only strings");
				continue;
			}
			list.add(item.getAsString());
		}

		return list;

	}

}
