package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigGearNiobium;
import yuudaari.soulus.common.registration.Registration;

@ConfigInjected(Soulus.MODID)
public class GearNiobium extends Registration.Item {

	@Inject public static ConfigGearNiobium CONFIG;

	public GearNiobium () {
		super("gear_niobium");
		setHasGlint();
		setHasDescription();

		Soulus.onConfigReload( () -> setMaxStackSize(CONFIG.stackSize));
	}
}
