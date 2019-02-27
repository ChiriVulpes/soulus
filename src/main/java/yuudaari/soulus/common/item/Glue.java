package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigGlue;
import yuudaari.soulus.common.registration.Registration;

@ConfigInjected(Soulus.MODID)
public class Glue extends Registration.ItemFood {

	@Inject public static ConfigGlue CONFIG;

	public Glue () {
		super("glue", () -> CONFIG);
		addOreDict("slimeball");
		setHasDescription();
	}
}
