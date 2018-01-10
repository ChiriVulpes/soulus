package yuudaari.soulus.common.item;

/*
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
*/
import yuudaari.soulus.common.util.ModItem;

public class SoulCatalyst extends ModItem {
	public SoulCatalyst() {
		super("soul_catalyst");
		glint = true;
	}
	/*
		@Override
		public void onRegisterRecipes(IForgeRegistry<IRecipe> registry) {
			registry.register(new RecipeComposerShaped(this, //
					"MGM", //
					"GBG", //
					"MGM", //
					'M', OrbMurky.INSTANCE.getFilledStack(), // 
					'G', ModItems.GEAR_OSCILLATING, 'B', CrystalBlood.INSTANCE.getFilledStack() //
			).setRegistryName(getRegistryName()));
		}
	*/

}