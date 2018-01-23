package yuudaari.soulus.common.config.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.ModPotionEffect;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "item/misc", id = Soulus.MODID, path = "glue")
@Serializable
public class ConfigGlue extends ConfigFood {

	@Serialized public boolean alwaysEdible = true;
	@Serialized public int amount = 1;
	@Serialized public float saturation = 0;
	@Serialized public int duration = 32;
	@Serialized public int quantity = 1;
	@Serialized public ModPotionEffect[] effects = new ModPotionEffect[] {
		new ModPotionEffect("nausea", 200)
	};

	@Override
	public boolean isAlwaysEdible () {
		return alwaysEdible;
	}

	@Override
	public int getAmount () {
		return amount;
	}

	@Override
	public float getSaturation () {
		return saturation;
	}

	@Override
	public int getDuration () {
		return duration;
	}

	@Override
	public int getQuantity () {
		return quantity;
	}

	@Override
	public ModPotionEffect[] getEffects () {
		return effects;
	}
}
