package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.ModItem;

public class Bone extends ModItem {

	public Bone (String name) {
		super(name);
		addOreDict("bone");
		setHasDescription();
	}

	@Override
	public String getDescriptionRegistryName () {
		return Soulus.MODID + ":bone";
	}
}
