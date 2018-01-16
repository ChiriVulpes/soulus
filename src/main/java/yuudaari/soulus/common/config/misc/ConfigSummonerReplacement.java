package yuudaari.soulus.common.config.misc;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.MapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/summoner_replacement", id = Soulus.MODID)
@Serializable
public class ConfigSummonerReplacement {

	@Serialized(value = Serializer.class, topLevel = true) public Map<String, ConfigStructure> structures = new HashMap<>();
	{
		structures.put("*", new ConfigStructure().addReplacement("*", EndersteelType.NORMAL));
		structures.put("mineshaft", new ConfigStructure().addReplacement("*", EndersteelType.STONE));
		structures.put("stronghold", new ConfigStructure().addReplacement("*", EndersteelType.END_STONE));
		structures.put("mansion", new ConfigStructure().addReplacement("*", EndersteelType.WOOD));
		structures.put("fortress", new ConfigStructure().addReplacement("*", EndersteelType.BLAZE));
	}

	public static class Serializer extends DefaultMapSerializer<ConfigStructure> {

		@Override
		public Class<ConfigStructure> getValueClass () {
			return ConfigStructure.class;
		}
	}

	@Serializable
	public static class ConfigStructure {

		@Serialized(value = Serializer.class, topLevel = true) public Map<String, EndersteelType> endersteelTypesByCreature = new HashMap<>();

		public ConfigStructure addReplacement (String creature, EndersteelType type) {
			endersteelTypesByCreature.put(creature, type);
			return this;
		}

		public static class Serializer extends MapSerializer.OfStringKeys<EndersteelType> {

			@Override
			public JsonElement serializeValue (EndersteelType value) throws Exception {
				return new JsonPrimitive(value.getName());
			}

			@Override
			public EndersteelType deserializeValue (JsonElement value) throws Exception {
				return EndersteelType.byName(value.getAsString());
			}
		}
	}
}
