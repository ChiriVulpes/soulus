package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/misc", id = Soulus.MODID, path = "emerald_bloody")
@Serializable
public class ConfigEmeraldBloody {

	@Serialized public Range villagerDropAmount = new Range(-10, 3);
}
