package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.Registration;

public class Bone extends Registration.Item implements IBone {

	public Bone (final String name) {
		super(name);
		addOreDict("bone");
		setHasDescription();
	}

	@Override
	public String getDescriptionRegistryName () {
		return Soulus.MODID + ":bone";
	}
}
