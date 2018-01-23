package yuudaari.soulus.common.config.item;

import yuudaari.soulus.common.util.ModPotionEffect;

public abstract class ConfigFood {

	public boolean isAlwaysEdible () {
		return false;
	}

	public abstract int getAmount ();

	public abstract float getSaturation ();

	public int getDuration () {
		return 32;
	}

	public int getQuantity () {
		return 1;
	}

	public ModPotionEffect[] getEffects () {
		return new ModPotionEffect[0];
	}
}
