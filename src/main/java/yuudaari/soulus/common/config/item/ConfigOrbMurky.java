package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/orb_murky", id = Soulus.MODID)
@Serializable
public class ConfigOrbMurky {

	@Serialized public int requiredEssence = 128;
}
