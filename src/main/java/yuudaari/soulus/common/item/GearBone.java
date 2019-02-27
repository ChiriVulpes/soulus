package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.Registration;

public class GearBone extends Registration.Item {

	public GearBone (String name) {
		super(name);
		addOreDict("gearBone");
		setHasDescription();
	}

	@Override
	public String getDescriptionRegistryName () {
		return Soulus.MODID + ":gear_bone";
	}
}
