package yuudaari.soulus.common.config.summoner_replacement;

import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigStructure {

	@Serialized(value = Serializer.class, topLevel = true) public Map<String, ConfigReplacement> replacementsByCreature = new HashMap<>();

	public ConfigStructure () {}

	public ConfigStructure (ConfigReplacement defaultReplacement) {
		addReplacement("*", defaultReplacement);
	}

	public ConfigStructure (EndersteelType type) {
		addReplacement("*", new ConfigReplacement(type));
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
