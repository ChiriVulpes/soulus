package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/gear_niobium", id = Soulus.MODID)
@Serializable
public class ConfigGearNiobium extends ConfigItem {

	{
		stackSize = 16;
	}

	@Serialized public Range xp = new Range(8, 10);
}
