package yuudaari.soulus.common.config.misc;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.config.ConfigProfile;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/breeding", id = Soulus.MODID, profile = "none")
@Serializable
public class ConfigBreeding {

	@ConfigProfile public static ConfigBreeding none = new ConfigBreeding().blacklist("*");
	@ConfigProfile public static ConfigBreeding all = new ConfigBreeding();

	@Serialized(value = ConfigBreedingMapSerializer.class, topLevel = true) public Map<String, Double> breeding = new HashMap<>();

	public ConfigBreeding blacklist (String creature) {
		breeding.put(creature, 0.0);
		return this;
	}

	public ConfigBreeding whitelist (String creature) {
		breeding.put(creature, 1.0);
		return this;
	}

	public ConfigBreeding setChance (String creature, double chance) {
		breeding.put(creature, chance);
		return this;
	}

	public double getChance (String creature) {
		if (breeding.containsKey(creature)) return breeding.get(creature);

		String domain = new ResourceLocation(creature).getResourceDomain() + ":" + "*";
		if (breeding.containsKey(domain)) return breeding.get(domain);

		return breeding.getOrDefault("*", 1.0);
	}

	public static class ConfigBreedingMapSerializer extends DefaultMapSerializer.OfStringKeys<Double> {

		@Override
		public Class<Double> getValueClass () {
			return Double.class;
		}
	}
}
