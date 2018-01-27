package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigGlue;
import yuudaari.soulus.common.util.ModItem;

@ConfigInjected(Soulus.MODID)
public class Glue extends ModItem {

	@Inject public static ConfigGlue CONFIG;

	public Glue () {
		super("glue");
		addOreDict("slimeball");
		setFood( () -> CONFIG);
		setHasDescription();
	}
}
