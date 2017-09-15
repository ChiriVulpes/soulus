package yuudaari.souls.common.item;

import yuudaari.souls.common.ModObjects;

public class EmeraldCoated extends SoulsItem {
	public EmeraldCoated() {
		super("emerald_coated");
	}

	@Override
	public void registerRecipes() {
		addFurnaceRecipe(ModObjects.BLOOD_CRYSTAL, 1.0F);
	}
}