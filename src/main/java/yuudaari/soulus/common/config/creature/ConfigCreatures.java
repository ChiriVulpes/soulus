package yuudaari.soulus.common.config.creature;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "creatures", id = Soulus.MODID)
@Serializable
public class ConfigCreatures {

	@Serialized(value = DimensionMapSerializer.class, topLevel = true) public Map<String, ConfigCreatureDimension> dimensionConfigs;
	{
		final Map<String, ConfigCreature> creatureConfigs = new HashMap<>();
		creatureConfigs.put("*", new ConfigCreature(0.0).setWhitelistedDrops("summoned", "*"));
		creatureConfigs.put("minecraft:skeleton", new ConfigCreature(0.0).setBlacklistedDrops("all", "minecraft:bone"));
		creatureConfigs
			.put("minecraft:wither_skeleton", new ConfigCreature(0.0).setBlacklistedDrops("all", "minecraft:bone"));
		creatureConfigs.put("minecraft:stray", new ConfigCreature(0.0).setBlacklistedDrops("all", "minecraft:bone"));

		final Map<String, ConfigCreatureBiome> biomeConfigs = new HashMap<>();
		biomeConfigs.put("*", new ConfigCreatureBiome(creatureConfigs));

		dimensionConfigs = new HashMap<>();
		dimensionConfigs.put("*", new ConfigCreatureDimension(biomeConfigs));

		for (final Map.Entry<String, ConfigCreatureDimension> entry : dimensionConfigs.entrySet()) {
			entry.getValue().dimensionId = entry.getKey();
		}
	}

	public static class DimensionMapSerializer extends DefaultMapSerializer<ConfigCreatureDimension> {

		@Override
		public Class<ConfigCreatureDimension> getValueClass () {
			return ConfigCreatureDimension.class;
		}

		@Override
		public Map<String, ConfigCreatureDimension> deserialize (final Class<?> requestedType, final JsonElement json) {
			final Map<String, ConfigCreatureDimension> result = super.deserialize(requestedType, json);
			if (result == null) return result;

			for (final Map.Entry<String, ConfigCreatureDimension> entry : result.entrySet()) {
				entry.getValue().dimensionId = entry.getKey();
			}

			return result;
		}
	}
}
