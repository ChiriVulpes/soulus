package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/glue", id = Soulus.MODID)
@Serializable
public class ConfigGlue extends ConfigFood {

	@Serialized public boolean foodAlwaysEdible = true;
	@Serialized public int foodAmount = 1;
	@Serialized public float foodSaturation = 0;
	@Serialized public int foodDuration = 32;
	@Serialized public int foodQuantity = 1;
	@Serialized public ModPotionEffect[] foodEffects = new ModPotionEffect[] {
		new ModPotionEffect("nausea", 200)
	};
}
