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

	public static class CreatureMapSerializer extends DefaultMapSerializer<ConfigCreature> {

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

	public ConfigCreatureBiome () {}

	public ConfigCreatureBiome (final Map<String, ConfigCreature> creatureConfigs) {
		this.creatureConfigs = creatureConfigs;

		for (final Map.Entry<String, ConfigCreature> entry : creatureConfigs.entrySet()) {
			entry.getValue().creatureId = entry.getKey();
		}
	}
}
