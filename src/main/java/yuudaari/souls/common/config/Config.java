package yuudaari.souls.common.config;

import yuudaari.souls.common.ModGenerators;
import yuudaari.souls.common.util.BoneType;
import yuudaari.souls.common.util.Logger;
import yuudaari.souls.common.world.generators.GeneratorFossils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Map;

public class Config {

	private static final String configFileName = "souls.json";

	public Map<BoneType, Map<String, EssenceDropConfig>> drops = EssenceDropConfig.getDefaultDropMap();
	public Map<String, SoulConfig> souls = SoulConfig.getDefaultSoulMap();

	/* SERIALIZER */

	private static final Serializer<Config> serializer;
	static {
		serializer = new Serializer<>(Config.class);

		serializer.fieldHandlers.put("drops", new FieldSerializer<Map<BoneType, Map<String, EssenceDropConfig>>>(
				Config::serializeDrops, Config::deserializeDrops));
		serializer.fieldHandlers.put("souls",
				new FieldSerializer<Map<String, SoulConfig>>(Config::serializeSouls, Config::deserializeSouls));

		serializer.otherHandlers.put("fossilVeins", new OtherSerializer(
				from -> GeneratorFossils.serialize(ModGenerators.GENERATOR_FOSSILS), (from, into) -> {
					GeneratorFossils.deserialize(from, ModGenerators.GENERATOR_FOSSILS);
					return null;
				}));
	}

	/* STATIC METHODS */

	public static Config loadConfig(String configDirectory) {
		Config config = new Config();

		File configFile = new File(configDirectory + "/souls/" + configFileName);

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

}
