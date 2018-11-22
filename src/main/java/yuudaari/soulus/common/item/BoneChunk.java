package yuudaari.soulus.common.item;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.ModItem;

public class BoneChunk extends ModItem {

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
