package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.Registration;

public class BoneChunk extends Registration.Item {

	public BoneChunk (String name) {
		super(name);
		addOreDict("boneChunk");
		setHasDescription();
	}

	/////////////////////////////////////////
	// Jei
	//

	@Override
	public String getDescriptionRegistryName () {
		return Soulus.MODID + ":bone_chunk";
	}
}
