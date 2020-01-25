package yuudaari.soulus.common.config.creature;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.config.ConfigProfile;
import yuudaari.soulus.common.misc.SpawnType;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "creatures/creatures", id = Soulus.MODID, profile = "no_creatures_no_drops")
@Serializable
public class ConfigCreatures {

	@ConfigProfile public static ConfigCreatures noCreaturesNoDrops = new ConfigCreatures()
		.addCreatureConfig("minecraft:*", new ConfigCreature(0.0)
			.setWhitelistedDrops(SpawnType.SUMMONED, "*")
			.setBlacklistedDrops(SpawnType.SUMMONED_MALICE, "*"))
		.addCreatureConfig("minecraft:skeleton", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone")
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:wither_skeleton", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone")
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:stray", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone")
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:wither", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"))
		.addCreatureConfig("minecraft:ender_dragon", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"));

	@ConfigProfile public static ConfigCreatures noCreaturesYesDrops = new ConfigCreatures()
		.addCreatureConfig("minecraft:*", new ConfigCreature(0.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"))
		.addCreatureConfig("minecraft:skeleton", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither_skeleton", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone"))
		.addCreatureConfig("minecraft:stray", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither", new ConfigCreature(1.0))
		.addCreatureConfig("minecraft:ender_dragon", new ConfigCreature(1.0));

	@ConfigProfile public static ConfigCreatures noCreaturesNoDropsAllMods = new ConfigCreatures()
		.addCreatureConfig("*", new ConfigCreature(0.0)
			.setWhitelistedDrops(SpawnType.SUMMONED, "*"))
		.addCreatureConfig("minecraft:skeleton", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone")
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:wither_skeleton", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone")
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:stray", new ConfigCreature(0.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone")
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:wither", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"))
		.addCreatureConfig("minecraft:ender_dragon", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"));

	@ConfigProfile public static ConfigCreatures yesCreaturesNoDrops = new ConfigCreatures()
		.addCreatureConfig("minecraft:*", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:skeleton", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SUMMONED, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither_skeleton", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SUMMONED, "minecraft:bone"))
		.addCreatureConfig("minecraft:stray", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SUMMONED, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"))
		.addCreatureConfig("minecraft:ender_dragon", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"));

	@ConfigProfile public static ConfigCreatures yesCreaturesNoDropsAllMods = new ConfigCreatures()
		.addCreatureConfig("*", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SPAWNED, "*"))
		.addCreatureConfig("minecraft:skeleton", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SUMMONED, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither_skeleton", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SUMMONED, "minecraft:bone"))
		.addCreatureConfig("minecraft:stray", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.SUMMONED, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"))
		.addCreatureConfig("minecraft:ender_dragon", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"));

	@ConfigProfile public static ConfigCreatures yesCreaturesYesDrops = new ConfigCreatures()
		.addCreatureConfig("*", new ConfigCreature(1.0)
			.setWhitelistedDrops(SpawnType.ALL, "*"))
		.addCreatureConfig("minecraft:skeleton", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone"))
		.addCreatureConfig("minecraft:wither_skeleton", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone"))
		.addCreatureConfig("minecraft:stray", new ConfigCreature(1.0)
			.setBlacklistedDrops(SpawnType.ALL, "minecraft:bone"));



	@Serialized(value = DimensionMapSerializer.class, topLevel = true) public Map<String, ConfigCreatureDimension> dimensionConfigs = new HashMap<>();

	public ConfigCreatures addDimensionConfig (final String dimension, final ConfigCreatureDimension config) {
		dimensionConfigs.put(dimension, config);
		config.dimensionId = dimension;
		return this;
	}

	public ConfigCreatures addBiomeConfig (final String biome, final ConfigCreatureBiome config) {
		addDimensionConfig("*", new ConfigCreatureDimension(biome, config));
		return this;
	}

	public ConfigCreatures addCreatureConfig (final String creature, final ConfigCreature config) {
		ConfigCreatureBiome biomeConfig = getBiomeConfig("*");

		if (biomeConfig == null) {
			biomeConfig = new ConfigCreatureBiome(creature, config);
			addBiomeConfig("*", biomeConfig);

		} else {
			biomeConfig.creatureConfigs.put(creature, config);
		}

		return this;
	}

	public ConfigCreatureBiome getBiomeConfig (final String biome) {
		final ConfigCreatureDimension dimensionConfig = dimensionConfigs.get("*");
		if (dimensionConfig == null) return null;

		return dimensionConfig.biomeConfigs.get(biome);
	}

	public static class DimensionMapSerializer extends DefaultMapSerializer.OfStringKeys<ConfigCreatureDimension> {

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
