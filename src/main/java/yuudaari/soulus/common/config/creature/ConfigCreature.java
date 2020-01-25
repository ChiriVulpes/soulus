package yuudaari.soulus.common.config.creature;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.common.misc.SpawnType;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigCreature {

	public String creatureId;
	@Serialized public double spawnChance = 0;
	@Serialized(DropMapSerializer.class) public Map<SpawnType, ConfigCreatureDrops> drops = new HashMap<>();

	public static class DropMapSerializer extends DefaultMapSerializer<SpawnType, ConfigCreatureDrops> {

		@Override
		public String serializeKey (final SpawnType key) {
			return key.getName();
		}

		@Override
		public SpawnType deserializeKey (final String key) {
			return SpawnType.fromName(key);
		}

		@Override
		public Class<ConfigCreatureDrops> getValueClass () {
			return ConfigCreatureDrops.class;
		}
	}

	public ConfigCreature () {
	}

	public ConfigCreature (final double spawnChance) {
		this.spawnChance = spawnChance;
	}

	public ConfigCreature setWhitelistedDrops (final SpawnType spawnType, final String... whitelistedDrops) {
		ConfigCreatureDrops dc = drops.get(spawnType);
		if (dc == null)
			drops.put(spawnType, dc = new ConfigCreatureDrops());

		dc.whitelistedDrops = Arrays.asList(whitelistedDrops);
		return this;
	}

	/**
	 * Sets an explicit blacklist of drops. All other drops are whitelisted.
	 */
	public ConfigCreature setBlacklistedDrops (final SpawnType spawnType, final String... blacklistedDrops) {
		ConfigCreatureDrops dc = drops.get(spawnType);
		if (dc == null)
			drops.put(spawnType, dc = new ConfigCreatureDrops(true));

		dc.blacklistedDrops = Arrays.asList(blacklistedDrops);
		return this;
	}
}
