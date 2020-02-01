package yuudaari.soulus.common.item;

import yuudaari.soulus.common.registration.Registration;

public class Bonemeal extends Registration.Item {

	public Bonemeal (String name) {
		super(name);
		addOreDict("bonemealSoulus");
		setHasDescription();
	}
}
