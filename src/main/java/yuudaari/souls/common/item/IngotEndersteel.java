package yuudaari.souls.common.item;

import yuudaari.souls.common.ModObjects;

public class IngotEndersteel extends SoulsItem {
	public IngotEndersteel() {
		super("ingot_endersteel");
	}

	@Override
	public void registerRecipes() {
		addFurnaceRecipe(ModObjects.DUST_ENDER_IRON);
	}
}
