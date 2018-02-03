package yuudaari.soulus.common.config.world.summoner_replacement;

import yuudaari.soulus.common.block.EndersteelType;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigReplacement {

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
