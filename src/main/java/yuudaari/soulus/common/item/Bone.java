package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.Registration;

public class Bone extends Registration.Item {

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
