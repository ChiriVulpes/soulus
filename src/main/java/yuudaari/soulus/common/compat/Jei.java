package yuudaari.soulus.common.compat;

import java.util.List;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapedRecipesWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import scala.Tuple2;
import yuudaari.soulus.common.compat.jei.RecipeCategoryComposer;
import yuudaari.soulus.common.compat.jei.RecipeWrapperComposer;
import yuudaari.soulus.common.compat.jei.SubtypeInterpreterEssence;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

@JEIPlugin
public class Jei implements IModPlugin {

	@Override
	public void registerItemSubtypes (ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(ModItems.ESSENCE, new SubtypeInterpreterEssence());
	}

	@Override
	public void registerCategories (IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new RecipeCategoryComposer(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void register (IModRegistry registry) {

		JeiDescriptionRegistry descriptionRegistry = new JeiDescriptionRegistry();

		ModItems.registerDescriptions(descriptionRegistry);
		ModBlocks.registerDescriptions(descriptionRegistry);

		for (Tuple2<List<ItemStack>, String> description : descriptionRegistry.ingredients) {
			registry.addIngredientInfo(description._1(), ItemStack.class, "jei.description." + description._2());
		}

		registry.addRecipeCatalyst(ModBlocks.COMPOSER.getItemStack(), RecipeCategoryComposer.UID);

		registry.handleRecipes(RecipeComposerShaped.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(RecipeComposerShapeless.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);

		registry
			.handleRecipes(ShapedOreRecipe.class, recipe -> new RecipeWrapperComposer(recipe, true), RecipeCategoryComposer.UID);
		registry
			.handleRecipes(ShapedRecipes.class, recipe -> new RecipeWrapperComposer(recipe, true), RecipeCategoryComposer.UID);
		registry
			.handleRecipes(ShapelessOreRecipe.class, recipe -> new RecipeWrapperComposer(recipe, false), RecipeCategoryComposer.UID);
		registry
			.handleRecipes(ShapelessRecipes.class, recipe -> new RecipeWrapperComposer(recipe, false), RecipeCategoryComposer.UID);

		registry.addRecipes(ForgeRegistries.RECIPES.getValues(), RecipeCategoryComposer.UID);

	}

}
