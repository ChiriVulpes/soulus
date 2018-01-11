package yuudaari.soulus.common.item;

import yuudaari.soulus.common.util.ModItem;

public class Bonemeal extends ModItem {

	public Bonemeal (String name) {
		super(name);
		addOreDict("bonemeal");
		setHasDescription();
	}
}
