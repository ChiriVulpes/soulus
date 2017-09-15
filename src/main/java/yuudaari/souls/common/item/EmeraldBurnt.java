package yuudaari.souls.common.item;

import net.minecraft.init.Items;

public class EmeraldBurnt extends SoulsItem {
	public EmeraldBurnt() {
		super("emerald_burnt");
	}

	@Override
	public void registerRecipes() {
		addFurnaceRecipeAsOutput(Items.EMERALD);
	}
}