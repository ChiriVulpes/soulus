package yuudaari.soulus.common.config.misc;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.ClassSerializer;
import yuudaari.soulus.common.util.serializer.DefaultClassSerializer;
import yuudaari.soulus.common.util.serializer.DefaultFieldSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldSerializationHandler;

@ConfigFile(file = "misc/fossil_block_bone_chunk_drops", id = Soulus.MODID)
@Serializable(ConfigFossils.Serializer.class)
public class ConfigFossils {

	private Map<String, ConfigFossil> fossils = new HashMap<>();
	{
		fossils.put("soulus:fossil_dirt_ender", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_dirt_frozen", new ConfigFossil(BoneType.FROZEN, 2, 6));
		fossils.put("soulus:fossil_dirt_fungal", new ConfigFossil(BoneType.FUNGAL, 2, 6));
		fossils.put("soulus:fossil_dirt", new ConfigFossil(BoneType.NORMAL, 2, 6));
		fossils.put("soulus:fossil_end_stone", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_gravel_scale", new ConfigFossil(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_netherrack_ender", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_netherrack", new ConfigFossil(BoneType.NETHER, 2, 6));
		fossils.put("soulus:fossil_sand_ender", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_sand_scale", new ConfigFossil(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_sand", new ConfigFossil(BoneType.DRY, 2, 6));
		fossils.put("soulus:fossil_sand_red_scale", new ConfigFossil(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_sand_red_dry", new ConfigFossil(BoneType.DRY, 2, 6));
	}

	public ConfigFossil get (String id) {
		return fossils.get(id);
	}

	public static class Serializer extends ClassSerializer<ConfigFossils> {

		@Override
		public ConfigFossils instantiate (Class<?> cls) {
			return new ConfigFossils();
		}

		@Override
		public void serialize (final ConfigFossils config, final JsonObject object) {

			final IFieldSerializationHandler<Object> serializer = new DefaultFieldSerializer();

			try {
				for (final Map.Entry<String, ConfigFossil> entry : config.fossils.entrySet()) {
					object.add(entry.getKey(), DefaultClassSerializer
						.serializeValue(serializer, ConfigFossil.class, false, entry.getValue()));
				}
			} catch (final Exception e) {
				Logger.warn("Couldn't serialize fossils:");
				Logger.error(e);
			}
		}

		@Override
		public ConfigFossils deserialize (ConfigFossils config, JsonElement json) {
			if (json == null || !(json instanceof JsonObject)) {
				Logger.warn("Not a Json Object");
				return config;
			}


			final IFieldDeserializationHandler<Object> serializer = new DefaultFieldSerializer();

			final Map<String, ConfigFossil> fossils = new HashMap<>();
			try {
				for (Map.Entry<String, JsonElement> jsonConfig : json.getAsJsonObject().entrySet()) {
					fossils.put(jsonConfig.getKey(), (ConfigFossil) DefaultClassSerializer
						.deserializeValue(serializer, ConfigFossil.class, false, jsonConfig.getValue()));
				}

				config.fossils = fossils;
			} catch (Exception e) {
				Logger.warn("Unable to deserialize fossils:");
				Logger.error(e);
			}

			return config;
		}
	}

	@Serializable
	public static class ConfigFossil {

		@Serialized(BoneType.Serializer.class) public BoneType type;
		@Serialized public int min;
		@Serialized public int max;

		public ConfigFossil () {}

		public ConfigFossil (BoneType boneType, int min, int max) {
			this.type = boneType;
			this.min = min;
			this.max = max;
		}
	}
}
