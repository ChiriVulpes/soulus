package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigGearOscillating;
import yuudaari.soulus.common.registration.Registration;

@ConfigInjected(Soulus.MODID)
public class GearOscillating extends Registration.Item {

	@Inject public static ConfigGearOscillating CONFIG;

	public GearOscillating () {
		super("gear_oscillating");
		setHasGlint();
		setHasDescription();

		Soulus.onConfigReload( () -> setMaxStackSize(CONFIG.stackSize));
	}
}
