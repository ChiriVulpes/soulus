package yuudaari.soulus.common.compat;

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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.compat.jei.RecipeCategoryComposer;
import yuudaari.soulus.common.compat.jei.RecipeWrapperComposer;
import yuudaari.soulus.common.compat.jei.SubtypeInterpreterEssence;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.recipe.RecipeComposerShaped;
import yuudaari.soulus.common.recipe.RecipeComposerShapeless;

@JEIPlugin
public class Jei implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(Essence.INSTANCE, new SubtypeInterpreterEssence());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new RecipeCategoryComposer(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		registry.addRecipeCatalyst(ModBlocks.COMPOSER.getItemStack(), RecipeCategoryComposer.UID);

		registry.handleRecipes(RecipeComposerShaped.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);
		registry.handleRecipes(RecipeComposerShapeless.class, RecipeWrapperComposer::new, RecipeCategoryComposer.UID);

		registry.handleRecipes(ShapedOreRecipe.class, recipe -> new ShapedOreRecipeWrapper(jeiHelpers, recipe),
				RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapedRecipes.class, recipe -> new ShapedRecipesWrapper(jeiHelpers, recipe),
				RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapelessOreRecipe.class, recipe -> new ShapelessRecipeWrapper<>(jeiHelpers, recipe),
				RecipeCategoryComposer.UID);
		registry.handleRecipes(ShapelessRecipes.class, recipe -> new ShapelessRecipeWrapper<>(jeiHelpers, recipe),
				RecipeCategoryComposer.UID);

		registry.addRecipes(ForgeRegistries.RECIPES.getValues(), RecipeCategoryComposer.UID);

	}

}