package yuudaari.soulus.common.config.world.summoner_replacement;

import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.config.ConfigProfile;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "world/summoner_replacement/replacement", id = Soulus.MODID, profile = "enabled")
@Serializable
public class ConfigSummonerReplacement {

	@ConfigProfile public static ConfigSummonerReplacement disabled = new ConfigSummonerReplacement();
	@ConfigProfile public static ConfigSummonerReplacement enabled = new ConfigSummonerReplacement()
		.addStructure("*", EndersteelType.NORMAL)
		.addStructure("mineshaft", EndersteelType.SPOOKY)
		.addStructure("stronghold", EndersteelType.ENDER)
		.addStructure("mansion", EndersteelType.EARTHY)
		.addStructure("fortress", EndersteelType.BLAZING);
	@ConfigProfile public static ConfigSummonerReplacement enabledEmpty = new ConfigSummonerReplacement()
		.addStructure("*", new ConfigReplacement(EndersteelType.NORMAL, false))
		.addStructure("mineshaft", new ConfigReplacement(EndersteelType.SPOOKY, false))
		.addStructure("stronghold", new ConfigReplacement(EndersteelType.ENDER, false))
		.addStructure("mansion", new ConfigReplacement(EndersteelType.EARTHY, false))
		.addStructure("fortress", new ConfigReplacement(EndersteelType.BLAZING, false));

	@Serialized(value = Serializer.class, topLevel = true) public Map<String, ConfigStructure> structures = new HashMap<>();

	public ConfigSummonerReplacement () {
	}

	public ConfigSummonerReplacement (ConfigStructure config) {
		structures.put("*", config);
	}

	public ConfigSummonerReplacement (ConfigReplacement config) {
		structures.put("*", new ConfigStructure(config));
	}

	public ConfigSummonerReplacement (EndersteelType type) {
		structures.put("*", new ConfigStructure(type));
	}

	public ConfigSummonerReplacement clear () {
		structures.clear();
		return this;
	}

	public ConfigSummonerReplacement addStructure (String name, ConfigStructure config) {
		structures.put(name, config);
		return this;
	}

	public ConfigSummonerReplacement addStructure (String name, ConfigReplacement config) {
		structures.put(name, new ConfigStructure(config));
		return this;
	}

	public ConfigSummonerReplacement addStructure (String name, EndersteelType type) {
		structures.put(name, new ConfigStructure(type));
		return this;
	}

	public static class Serializer extends DefaultMapSerializer.OfStringKeys<ConfigStructure> {

		@Override
		public Class<ConfigStructure> getValueClass () {
			return ConfigStructure.class;
		}
	}
}
