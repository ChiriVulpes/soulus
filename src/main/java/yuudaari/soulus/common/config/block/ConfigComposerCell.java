package yuudaari.soulus.common.config.block;

import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.Soulus;

@ConfigFile(file = "block/composer", id = Soulus.MODID, path = "cell")
@Serializable
public class ConfigComposerCell {

	@Serialized public int maxQuantity = 64;
}
