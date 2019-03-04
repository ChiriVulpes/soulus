package yuudaari.soulus.common.config.item;

import yuudaari.soulus.common.util.serializer.Serialized;

public abstract class ConfigItem {

	@Serialized public int stackSize = 64;
}
