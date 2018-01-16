package yuudaari.soulus.common.world.generators;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.misc.ConfigOreVeins;
import yuudaari.soulus.common.world.ModGenerator;

@ConfigInjected(Soulus.MODID)
public class GeneratorFossils extends ModGenerator {

	@Inject(ConfigOreVeins.class) public static ConfigOreVeins CONFIG;

	public GeneratorFossils () {
		setVeins(CONFIG.veins);
	}
}
