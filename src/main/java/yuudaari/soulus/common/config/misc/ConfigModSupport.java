package yuudaari.soulus.common.config.misc;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/mod_support", id = Soulus.MODID)
@Serializable
public class ConfigModSupport {

	@Serialized public ConfigJEI jei = new ConfigJEI();

	@Serializable
	public class ConfigJEI {

		@Serialized public boolean showNormalRecipesInComposerTab = false;
	}

}
