package yuudaari.soulus.common.item;

import yuudaari.soulus.common.registration.Registration;

public class Bonemeal extends Registration.Item {

	public static String ORE_DICT = "bonemealSoulus";

	public Bonemeal (String name) {
		super(name);
		addOreDict(ORE_DICT);
		setHasDescription();
	}
}
