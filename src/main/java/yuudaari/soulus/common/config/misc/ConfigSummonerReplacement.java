package yuudaari.soulus.common.config.misc;

import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/summoner_replacement", id = Soulus.MODID)
@Serializable
public class ConfigSummonerReplacement {

	@Serialized(value = Serializer.class, topLevel = true) public Map<String, ConfigStructure> structures = new HashMap<>();
	{
		structures.put("*", new ConfigStructure(new ConfigReplacement(EndersteelType.NORMAL)));
		structures.put("mineshaft", new ConfigStructure(new ConfigReplacement(EndersteelType.STONE)));
		structures.put("stronghold", new ConfigStructure(new ConfigReplacement(EndersteelType.END_STONE)));
		structures.put("mansion", new ConfigStructure(new ConfigReplacement(EndersteelType.WOOD)));
		structures.put("fortress", new ConfigStructure(new ConfigReplacement(EndersteelType.BLAZE)));
	}

	public static class Serializer extends DefaultMapSerializer<ConfigStructure> {

		@Override
		public Class<ConfigStructure> getValueClass () {
			return ConfigStructure.class;
		}
	}

	@Serializable
	public static class ConfigStructure {

		@Serialized(value = Serializer.class, topLevel = true) public Map<String, ConfigReplacement> replacementsByCreature = new HashMap<>();

		public ConfigStructure () {}

		public ConfigStructure (ConfigReplacement defaultReplacement) {
			addReplacement("*", defaultReplacement);
		}

		public ConfigStructure addReplacement (String creature, ConfigReplacement replacement) {
			replacementsByCreature.put(creature, replacement);
			return this;
		}

		public static class Serializer extends DefaultMapSerializer<ConfigReplacement> {

			@Override
			public Class<ConfigReplacement> getValueClass () {
				return ConfigReplacement.class;
			}
		}
	}

	@Serializable
	public static class ConfigReplacement {

		@Serialized(EndersteelType.Serializer.class) public EndersteelType type;
		@Serialized public boolean midnightJewel;

		public ConfigReplacement () {}

		public ConfigReplacement (EndersteelType type) {
			this(type, true);
		}

		public ConfigReplacement (EndersteelType type, boolean hasMidnightJewel) {
			this.type = type;
			midnightJewel = hasMidnightJewel;
		}
	}
}
