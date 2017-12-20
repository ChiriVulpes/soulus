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
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.world.generators.GeneratorFossils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Config {

	private static final String configFileName = "soulus.json";

	public boolean replaceSpawnersWithSummoners = true;
	public int boneChunkParticleCount = 3;
	public List<CreatureConfig> creatures = CreatureConfig.getDefaultCreatureConfigs();

	public int getSoulbookQuantity(String mobTarget) {
		for (CreatureConfig config : creatures) {
			if (config.essence.equals(mobTarget)) {
				return config.soulbookQuantity;
			}
		}
		return -1;
	}

	/* SERIALIZER */

	private static final Serializer<Config> serializer;
	static {
		serializer = new Serializer<>(Config.class, "replaceSpawnersWithSummoners", "boneChunkParticleCount");

		serializer.fieldHandlers.put("creatures",
				new ManualSerializer(Config::serializeCreatures, Config::deserializeCreatures));

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

	private static JsonElement serializeCreatures(Object obj) {
		@SuppressWarnings("unchecked")
		List<CreatureConfig> creatureConfigs = (List<CreatureConfig>) obj;

		JsonArray creatures = new JsonArray();

		for (CreatureConfig creatureConfig : creatureConfigs) {
			creatures.add(CreatureConfig.serializer.serialize(creatureConfig));
		}

		return creatures;
	}

	private static Object deserializeCreatures(JsonElement json, Object current) {
		@SuppressWarnings("unchecked")
		List<CreatureConfig> creatureConfigs = (List<CreatureConfig>) current;

		JsonArray creatures = (JsonArray) json;
		if (creatures == null) {
			return current;
		}

		creatureConfigs.clear();

		for (JsonElement creatureConfig : creatures) {
			creatureConfigs
					.add((CreatureConfig) CreatureConfig.serializer.deserialize(creatureConfig, new CreatureConfig()));
		}

		return creatureConfigs;
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
