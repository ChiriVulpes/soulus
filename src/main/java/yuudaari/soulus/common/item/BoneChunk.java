package yuudaari.soulus.common.item;

import net.minecraft.item.crafting.Ingredient;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.util.ModItem;

public class BoneChunk extends ModItem {

	public BoneChunk (String name) {
		super(name);
		addOreDict("boneChunk");
	}

	/////////////////////////////////////////
	// Jei
	//

	@Override
	public void onRegisterDescription (JeiDescriptionRegistry registry) {
		registry.add(Ingredient.fromItem(this), Soulus.MODID + ":bone_chunk");
	}
}
