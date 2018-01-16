package yuudaari.soulus.common.config.essence;

import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigCreatureLoot {

	@Serialized public int min;
	@Serialized public int max;
	@Serialized public double chance;

	public ConfigCreatureLoot () {}

	public ConfigCreatureLoot (int min, int max, double chance) {
		this.min = min;
		this.max = max;
		this.chance = chance;
	}
}
