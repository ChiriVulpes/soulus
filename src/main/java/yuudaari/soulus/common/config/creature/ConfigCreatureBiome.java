package yuudaari.soulus.common.config.creature;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigCreatureBiome {

	public String biomeId;
	@Serialized(value = CreatureMapSerializer.class, topLevel = true) public Map<String, ConfigCreature> creatureConfigs = new HashMap<>();

	public ConfigCreatureBiome () {
	}

	public ConfigCreatureBiome (final String creature, final ConfigCreature config) {
		creatureConfigs.put(creature, config);
	}

	public ConfigCreatureBiome (final Map<String, ConfigCreature> configs) {
		creatureConfigs = configs;

		for (final Map.Entry<String, ConfigCreature> entry : configs.entrySet()) {
			entry.getValue().creatureId = entry.getKey();
		}
	}

	public static class CreatureMapSerializer extends DefaultMapSerializer.OfStringKeys<ConfigCreature> {

		@Override
		public Class<ConfigCreature> getValueClass () {
			return ConfigCreature.class;
		}

		@Override
		public Map<String, ConfigCreature> deserialize (final Class<?> requestedType, final JsonElement json) {
			final Map<String, ConfigCreature> result = super.deserialize(requestedType, json);
			if (result == null) return result;

			for (final Map.Entry<String, ConfigCreature> entry : result.entrySet()) {
				entry.getValue().creatureId = entry.getKey();
			}

			return result;
		}
	}
}
