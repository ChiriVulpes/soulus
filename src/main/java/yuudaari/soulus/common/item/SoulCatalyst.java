package yuudaari.soulus.common.item;

import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.recipe.Recipe;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.util.ModItem;

public class SoulCatalyst extends ModItem {
	public SoulCatalyst() {
		super("soul_catalyst");
		glint = true;

		addRecipe((Recipe) new RecipeComposerShaped(this, //
				"MGM", //
				"GBG", //
				"MGM", //
				'M', OrbMurky.INSTANCE.getFilledStack(), // 
				'G', ModItems.GEAR_OSCILLATING, 'B', BloodCrystal.INSTANCE.getFilledStack() //
		).setRegistryName(getRegistryName()));
	}
}