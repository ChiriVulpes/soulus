package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/misc", id = Soulus.MODID, path = "sledgehammer")
@Serializable
public class ConfigSledgehammer {

	@Serialized public int durability = 255;
	@Serialized public int durabilityEndersteel = 2047;
	@Serialized public int durabilityEndersteelDark = 4095;
	@Serialized public int durabilityNiobium = -1;
}
